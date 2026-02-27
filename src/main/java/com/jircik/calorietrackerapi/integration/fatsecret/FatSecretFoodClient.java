package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.integration.dto.FoodDetailsResponse;
import com.jircik.calorietrackerapi.integration.dto.FoodSearchResponse;
import com.jircik.calorietrackerapi.integration.dto.NutritionData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class FatSecretFoodClient {

    private final WebClient webClient;
    private final FatSecretAuthClient authClient;

    public FatSecretFoodClient(WebClient fatSecretApiWebClient,
                               FatSecretAuthClient authClient) {
        this.webClient = fatSecretApiWebClient;
        this.authClient = authClient;
    }

    public FoodSearchResponse.Food searchFirstFood(String foodName) {

        String token = authClient.getValidToken();

        FoodSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/foods/search/v1")
                        .queryParam("search_expression", foodName)
                        .queryParam("format", "json")
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(FoodSearchResponse.class)
                .block();

        if (response == null
                || response.foods() == null
                || response.foods().food() == null
                || response.foods().food().isEmpty()) {

            throw new RuntimeException("Food not found in FatSecret");
        }

        return response.foods().food().getFirst();
    }

    public FoodDetailsResponse getFoodById(String foodId) {

        String token = authClient.getValidToken();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/food/v5")
                        .queryParam("food_id", foodId)
                        .queryParam("format", "json")
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(FoodDetailsResponse.class)
                .block();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public NutritionData calculateNutrition(String foodName, Double quantityInGrams) {

        // 1. Buscar primeiro alimento
        FoodSearchResponse.Food food = searchFirstFood(foodName);

        // 2. Buscar detalhes
        FoodDetailsResponse details = getFoodById(food.food_id());

        var servings = details.food().servings().serving();

        if (servings == null || servings.isEmpty()) {
            throw new RuntimeException("No servings found for food");
        }

        // 3. Filtrar servings em gramas
        var gramServings = servings.stream()
                .filter(s -> "g".equalsIgnoreCase(s.metric_serving_unit()))
                .toList();

        if (gramServings.isEmpty()) {
            gramServings = servings;
        }

        // 4. Tentar encontrar 100g
        var baseServing = gramServings.stream()
                .filter(s -> s.serving_description() != null
                        && s.serving_description().contains("100"))
                .findFirst()
                .orElse(gramServings.getFirst());

        double baseAmount = Double.parseDouble(baseServing.metric_serving_amount());
        double baseCalories = Double.parseDouble(baseServing.calories());
        double baseCarbs = Double.parseDouble(baseServing.carbohydrate());
        double baseProtein = Double.parseDouble(baseServing.protein());
        double baseFat = Double.parseDouble(baseServing.fat());

        double factor = quantityInGrams / baseAmount;

        return new NutritionData(
                round(baseCalories * factor),
                round(baseCarbs * factor),
                round(baseProtein * factor),
                round(baseFat * factor)
        );
    }
}