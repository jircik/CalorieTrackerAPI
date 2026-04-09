package com.jircik.calorietrackerapi.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User's physical activity level")
public enum ActivityLevelEnum {
    SEDENTARY,
    LIGHT,
    MODERATE,
    INTENSE
}
