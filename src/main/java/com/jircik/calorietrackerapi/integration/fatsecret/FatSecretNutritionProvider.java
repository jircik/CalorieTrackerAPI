package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.domain.fatsecret.NutritionProvider;
import com.jircik.calorietrackerapi.domain.fatsecret.NutritionResult;
import com.jircik.calorietrackerapi.integration.dto.FoodDetailsResponse;
import com.jircik.calorietrackerapi.integration.dto.FoodSearchResponse;
import org.springframework.stereotype.Service;

@Service
public class FatSecretNutritionProvider implements NutritionProvider {

    private final FatSecretFoodClient foodClient;

    public FatSecretNutritionProvider(FatSecretFoodClient foodClient) {
        this.foodClient = foodClient;
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
                details.food().food_name(),
                details.food().food_name(),
                round(baseCalories * factor),
                round(baseCarbs * factor),
                round(baseProtein * factor),
                round(baseFat * factor)
        );
    }

    public NutritionResult calculateNutritionByFoodId(String foodId, Double quantityInGrams) {

        if (foodId == null || foodId.isBlank()) {
            throw new IllegalArgumentException("Food id must not be null");
        }

        if (quantityInGrams == null || quantityInGrams <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        FoodDetailsResponse details = foodClient.getFoodById(foodId);

        return calculateFromDetails(details, quantityInGrams);
    }

    @Override
    public NutritionResult getNutrition(String foodName, Double quantity) {

        if (foodName == null || foodName.isBlank()) {
            throw new IllegalArgumentException("Food name must not be null or blank");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        FoodSearchResponse.Food food = foodClient.searchFirstFood(foodName);

        return calculateNutritionByFoodId(food.food_id(), quantity);
    }
}