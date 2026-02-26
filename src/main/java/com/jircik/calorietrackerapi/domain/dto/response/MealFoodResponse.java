package com.jircik.calorietrackerapi.domain.dto.response;

public record MealFoodResponse(
        Long id,
        String foodName,
        Double quantity,
        String unit,
        Double calories,
        Double carbs,
        Double protein,
        Double fat
) {
}
