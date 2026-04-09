package com.jircik.calorietrackerapi.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MealFoodResponse(

        @Schema(description = "Unique meal food entry ID")
        Long id,

        @Schema(description = "Name of the food as entered by the user")
        String foodName,

        @Schema(description = "Quantity of the food")
        Double quantity,

        @Schema(description = "Unit of measurement as entered by the user (e.g. g, ml)")
        String unit,

        @Schema(description = "Total calories for this quantity, rounded to 2 decimal places")
        Double calories,

        @Schema(description = "Total carbohydrates in grams for this quantity, rounded to 2 decimal places")
        Double carbs,

        @Schema(description = "Total protein in grams for this quantity, rounded to 2 decimal places")
        Double protein,

        @Schema(description = "Total fat in grams for this quantity, rounded to 2 decimal places")
        Double fat

) {}
