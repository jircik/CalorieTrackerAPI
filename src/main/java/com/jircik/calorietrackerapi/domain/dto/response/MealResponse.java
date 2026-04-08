package com.jircik.calorietrackerapi.domain.dto.response;

import com.jircik.calorietrackerapi.domain.entity.MealTypeEnum;

import java.time.LocalDateTime;

public record MealResponse(
        Long id,
        Long userId,
        LocalDateTime dateTime,
        MealTypeEnum mealType,
        LocalDateTime createdAt
) {
}
