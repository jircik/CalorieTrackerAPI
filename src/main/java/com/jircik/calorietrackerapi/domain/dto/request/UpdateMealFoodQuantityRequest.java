package com.jircik.calorietrackerapi.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateMealFoodQuantityRequest(
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Double quantity
) {
}
