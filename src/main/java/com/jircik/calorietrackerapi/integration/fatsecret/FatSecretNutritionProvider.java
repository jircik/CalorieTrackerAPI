package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.domain.fatsecret.NutritionProvider;
import com.jircik.calorietrackerapi.domain.fatsecret.NutritionResult;
import org.springframework.stereotype.Service;

@Service
public class FatSecretNutritionProvider implements NutritionProvider {

    private final FatSecretFoodClient foodClient;

    public FatSecretNutritionProvider(FatSecretFoodClient foodClient) {
        this.foodClient = foodClient;
    }

    @Override
    public NutritionResult getNutrition(String foodName, Double quantity) {
        return foodClient.calculateNutrition(foodName, quantity);
    }
}