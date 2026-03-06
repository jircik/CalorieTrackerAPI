package com.jircik.calorietrackerapi.domain.fatsecret;

public interface NutritionProvider {
    NutritionResult getNutrition(String foodName, Double quantity);
    NutritionResult calculateNutritionByFoodId(String foodId, Double quantityInGrams);
}