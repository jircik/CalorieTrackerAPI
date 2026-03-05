package com.jircik.calorietrackerapi.domain.fatsecret;

public interface NutritionProvider {
    NutritionResult getNutrition(String foodName, Double quantity);
}