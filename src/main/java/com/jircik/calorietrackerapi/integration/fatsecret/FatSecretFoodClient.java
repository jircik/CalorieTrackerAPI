package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.exception.IntegrationException;
import com.jircik.calorietrackerapi.integration.dto.FoodDetailsResponse;
import com.jircik.calorietrackerapi.integration.dto.FoodSearchResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class FatSecretFoodClient {

    private final WebClient webClient;
    private final FatSecretAuthClient authClient;
    private final Cache<String, FoodDetailsResponse> foodDetailsCache;
    private final Cache<String, FoodSearchResponse.Food> foodSearchCache;


    public FatSecretFoodClient(WebClient fatSecretApiWebClient,
                               FatSecretAuthClient authClient,
                               Cache<String, FoodDetailsResponse> foodDetailsCache, Cache<String, FoodSearchResponse.Food> foodSearchCache) {
        this.webClient = fatSecretApiWebClient;
        this.authClient = authClient;
        this.foodDetailsCache = foodDetailsCache;
        this.foodSearchCache = foodSearchCache;
    }

    private String normalizeFoodName(String foodName) {
        return foodName.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    public FoodSearchResponse.Food searchFirstFood(String foodName) {

        String normalized =  normalizeFoodName(foodName);

        FoodSearchResponse.Food cached = foodSearchCache.getIfPresent(normalized);

        if (cached != null) {
            return cached;
        }

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

        FoodSearchResponse.Food result = response.foods().food().getFirst();

        foodSearchCache.put(normalized, result);

        return result;
    }

    public FoodDetailsResponse getFoodById(String foodId) {

        FoodDetailsResponse cached = foodDetailsCache.getIfPresent(foodId);

        if (cached != null){
            return cached;
        }

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

        foodDetailsCache.put(foodId, response);

        return response;
    }
}