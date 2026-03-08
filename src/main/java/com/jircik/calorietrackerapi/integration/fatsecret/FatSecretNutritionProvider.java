package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.domain.entity.Food;
import com.jircik.calorietrackerapi.domain.entity.FoodNutrition;
import com.jircik.calorietrackerapi.domain.fatsecret.NutritionProvider;
import com.jircik.calorietrackerapi.domain.fatsecret.NutritionResult;
import com.jircik.calorietrackerapi.integration.dto.FoodDetailsResponse;
import com.jircik.calorietrackerapi.integration.dto.FoodSearchResponse;
import com.jircik.calorietrackerapi.repository.FoodNutritionRepository;
import com.jircik.calorietrackerapi.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FatSecretNutritionProvider implements NutritionProvider {

    private final FatSecretFoodClient foodClient;
    private final FoodRepository foodRepository;
    private final FoodNutritionRepository foodNutritionRepository;

    public FatSecretNutritionProvider(
            FatSecretFoodClient foodClient,
            FoodRepository foodRepository, FoodNutritionRepository foodNutritionRepository) {
        this.foodClient = foodClient;
        this.foodRepository = foodRepository;
        this.foodNutritionRepository = foodNutritionRepository;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private NutritionResult calculateFromDetails(
            FoodDetailsResponse details,
            Double quantityInGrams
    ) {

        var servings = details.food().servings().serving();

        if (servings == null || servings.isEmpty()) {
            throw new RuntimeException("No servings found for food");
        }

        var gramServings = servings.stream()
                .filter(s -> "g".equalsIgnoreCase(s.metric_serving_unit()))
                .toList();

        if (gramServings.isEmpty()) {
            gramServings = servings;
        }

        var baseServing = gramServings.stream()
                .filter(s -> s.serving_description() != null &&
                        s.serving_description().contains("100"))
                .findFirst()
                .orElse(gramServings.getFirst());

        double baseAmount = Double.parseDouble(baseServing.metric_serving_amount());
        double baseCalories = Double.parseDouble(baseServing.calories());
        double baseCarbs = Double.parseDouble(baseServing.carbohydrate());
        double baseProtein = Double.parseDouble(baseServing.protein());
        double baseFat = Double.parseDouble(baseServing.fat());

        double factor = quantityInGrams / baseAmount;

        return new NutritionResult(
                details.food().food_id(),
                details.food().food_name(),
                round(baseCalories * factor),
                round(baseCarbs * factor),
                round(baseProtein * factor),
                round(baseFat * factor)
        );
    }

    @Override
    public NutritionResult calculateNutritionByFoodId(String foodId, Double quantityInGrams) {

        if (foodId == null || foodId.isBlank()) {
            throw new IllegalArgumentException("Food id must not be null");
        }

        if (quantityInGrams == null || quantityInGrams <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        FoodNutrition cached = foodNutritionRepository.findById(foodId).orElse(null);

        if (cached != null) {
            double factor = quantityInGrams / 100.0;

            return new NutritionResult(
                    foodId,
                    null,
                    round(cached.getCaloriesPer100g() * factor),
                    round(cached.getCarbsPer100g() * factor),
                    round(cached.getProteinPer100g() * factor),
                    round(cached.getFatPer100g() * factor)
            );
        }

        FoodDetailsResponse details = foodClient.getFoodById(foodId);

        NutritionResult result = calculateFromDetails(details, quantityInGrams);

        FoodNutrition nutrition = FoodNutrition.builder()
                .fatSecretFoodId(foodId)
                .caloriesPer100g(result.calories() * (100.0 / quantityInGrams))
                .carbsPer100g(result.carbs() * (100.0 / quantityInGrams))
                .proteinPer100g(result.protein() * (100.0 / quantityInGrams))
                .fatPer100g(result.fat() * (100.0 / quantityInGrams))
                .build();

        foodNutritionRepository.save(nutrition);

        return result;
    }

    @Override
    public NutritionResult getNutrition(String foodName, Double quantity) {

        if (foodName == null || foodName.isBlank()) {
            throw new IllegalArgumentException("Food name must not be null or blank");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        String normalized = foodName.trim().toLowerCase();

        Optional<Food> existingFood = foodRepository.findByNameIgnoreCase(normalized);

        if (existingFood.isPresent()) {
            return calculateNutritionByFoodId(
                    existingFood.get().getFatSecretFoodId(),
                    quantity
            );
        }

        FoodSearchResponse.Food food = foodClient.searchFirstFood(foodName);

        Food newFood = Food.builder()
                .name(normalized)
                .fatSecretFoodId(food.food_id())
                .build();

        foodRepository.save(newFood);

        return calculateNutritionByFoodId(food.food_id(), quantity);
    }
}