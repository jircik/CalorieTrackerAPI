package com.jircik.calorietrackerapi.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateMealRequest(

        @NotNull(message = "UserId is required")
        Long userId,

        @NotNull(message = "DateTime is required")
        LocalDateTime dateTime) {
}
