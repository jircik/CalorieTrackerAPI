package com.jircik.calorietrackerapi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateMealFoodQuantityRequest(

        @Schema(description = "New quantity of the food (triggers macro recalculation)", example = "200.0")
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Double quantity

) {}
