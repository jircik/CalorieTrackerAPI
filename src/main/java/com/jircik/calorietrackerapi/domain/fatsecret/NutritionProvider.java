package com.jircik.calorietrackerapi.domain.fatsecret;

import com.jircik.calorietrackerapi.integration.dto.NutritionData;

public interface NutritionProvider {
    NutritionData getNutrition(String foodName, Double quantity);
}