package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.exception.IntegrationException;
import com.jircik.calorietrackerapi.integration.dto.FoodDetailsResponse;
import com.jircik.calorietrackerapi.integration.dto.FoodSearchResponse;
import com.jircik.calorietrackerapi.integration.dto.NutritionData;
import org.springframework.http.HttpStatusCode;
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
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new IntegrationException("FatSecret client error: " + body))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new IntegrationException("FatSecret server error: " + body))
                )
                .bodyToMono(FoodSearchResponse.class)
                .block();

        if (response == null
                || response.foods() == null
                || response.foods().food() == null
                || response.foods().food().isEmpty()) {

            throw new IntegrationException("Invalid response from FatSecret (searchFirstFood)");
        }

        return response.foods().food().getFirst();
    }

    public FoodDetailsResponse getFoodById(String foodId) {

        String token = authClient.getValidToken();

        FoodDetailsResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rest/food/v5")
                        .queryParam("food_id", foodId)
                        .queryParam("format", "json")
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new IntegrationException(
                                        "FatSecret client error (getFoodById): " + body))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new IntegrationException(
                                        "FatSecret server error (getFoodById): " + body))
                )
                .bodyToMono(FoodDetailsResponse.class)
                .block();

        if (response == null || response.food() == null) {
            throw new IntegrationException("Invalid response from FatSecret (getFoodById)");
        }

        return response;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public NutritionData calculateNutrition(String foodName, Double quantityInGrams) {

        if (foodName == null || foodName.isBlank()) {
            throw new IllegalArgumentException("Food name must not be null or blank");
        }

        if (quantityInGrams == null || quantityInGrams <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        FoodSearchResponse.Food food = searchFirstFood(foodName);

        if (food == null || food.food_id() == null) {
            throw new IntegrationException("Invalid search result from FatSecret");
        }

        FoodDetailsResponse details = getFoodById(food.food_id());

        if (details == null
                || details.food() == null
                || details.food().servings() == null
                || details.food().servings().serving() == null
                || details.food().servings().serving().isEmpty()) {

            throw new IntegrationException("No servings found for food");
        }

        var servings = details.food().servings().serving();

        // Filtra apenas servings com unidade em gramas
        var gramServings = servings.stream()
                .filter(s -> s.metric_serving_unit() != null
                        && s.metric_serving_unit().equalsIgnoreCase("g"))
                .toList();

        if (gramServings.isEmpty()) {
            gramServings = servings;
        }

        var baseServing = gramServings.stream()
                .filter(s -> s.serving_description() != null
                        && s.serving_description().contains("100"))
                .findFirst()
                .orElse(gramServings.getFirst());

        double baseAmount;
        double baseCalories;
        double baseCarbs;
        double baseProtein;
        double baseFat;

        try {
            baseAmount = Double.parseDouble(baseServing.metric_serving_amount());
            baseCalories = Double.parseDouble(baseServing.calories());
            baseCarbs = Double.parseDouble(baseServing.carbohydrate());
            baseProtein = Double.parseDouble(baseServing.protein());
            baseFat = Double.parseDouble(baseServing.fat());
        } catch (Exception e) {
            throw new IntegrationException("Invalid numeric values from FatSecret");
        }

        if (baseAmount <= 0) {
            throw new IntegrationException("Invalid serving amount from FatSecret");
        }

        double factor = quantityInGrams / baseAmount;

        return new NutritionData(
                round(baseCalories * factor),
                round(baseCarbs * factor),
                round(baseProtein * factor),
                round(baseFat * factor)
        );
    }
}