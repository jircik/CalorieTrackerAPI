package com.jircik.calorietrackerapi.domain.dto.response;

import java.time.LocalDateTime;

public record MealResponse(
        Long id,
        Long userId,
        LocalDateTime dateTime,
        LocalDateTime createdAt
) {
}
