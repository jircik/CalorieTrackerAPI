package com.jircik.calorietrackerapi.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MealSummaryResponse(

        @Schema(description = "ID of the meal")
        Long mealId,

        @Schema(description = "Total calories across all foods in this meal")
        Double totalCalories,

        @Schema(description = "Total protein in grams across all foods in this meal")
        Double totalProtein,

        @Schema(description = "Total carbohydrates in grams across all foods in this meal")
        Double totalCarbs,

        @Schema(description = "Total fat in grams across all foods in this meal")
        Double totalFat,

        @Schema(description = "Number of food entries in this meal")
        Double foodCount

) {}
