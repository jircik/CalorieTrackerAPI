package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.domain.fatsecret.NutritionProvider;
import com.jircik.calorietrackerapi.integration.dto.NutritionData;
import org.springframework.stereotype.Service;

@Service
public class FatSecretNutritionProvider implements NutritionProvider {

    private final FatSecretFoodClient foodClient;

    public FatSecretNutritionProvider(FatSecretFoodClient foodClient) {
        this.foodClient = foodClient;
    }

    @Override
    public NutritionData getNutrition(String foodName, Double quantity) {
        return foodClient.calculateNutrition(foodName, quantity);
    }
}