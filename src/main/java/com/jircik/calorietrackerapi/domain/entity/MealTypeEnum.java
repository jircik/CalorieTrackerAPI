package com.jircik.calorietrackerapi.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type of meal within a day")
public enum MealTypeEnum {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACKS
}
