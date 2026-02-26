package com.jircik.calorietrackerapi.domain.dto.response;

public record MealSummaryResponse(
        Long mealId,
        Double totalCalories,
        Double totalProtein,
        Double totalCarbs,
        Double totalFat,
        Double foodCount
) {
}
