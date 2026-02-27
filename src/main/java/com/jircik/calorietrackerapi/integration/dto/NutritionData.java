package com.jircik.calorietrackerapi.integration.dto;

public record NutritionData(
        Double calories,
        Double carbs,
        Double protein,
        Double fat
) {}
