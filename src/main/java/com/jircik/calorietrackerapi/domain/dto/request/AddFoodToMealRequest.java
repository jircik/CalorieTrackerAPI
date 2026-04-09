package com.jircik.calorietrackerapi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddFoodToMealRequest(

        @Schema(description = "Name of the food to look up via FatSecret", example = "chicken breast")
        @NotBlank(message = "Food Name is required")
        String foodName,

        @Schema(description = "Quantity of the food", example = "150.0")
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Double quantity,

        @Schema(description = "Unit of measurement (treated as grams for macro calculation)", example = "g")
        @NotBlank(message = "Unit is required")
        String unit

) {}
