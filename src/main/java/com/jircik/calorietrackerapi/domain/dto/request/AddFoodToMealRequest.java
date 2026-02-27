package com.jircik.calorietrackerapi.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddFoodToMealRequest(

        @NotBlank(message = "Food Name is required")
        String foodName,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        Double quantity,

        @NotBlank(message = "Unit is required")
        String unit

) {

}
