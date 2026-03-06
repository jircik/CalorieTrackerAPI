package com.jircik.calorietrackerapi.domain.fatsecret;

public record NutritionResult(
        String foodId,
        String foodName,
        double calories,
        double carbs,
        double protein,
        double fat
) {
}
