package com.jircik.calorietrackerapi.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User's gender")
public enum GenderEnum {
    MALE,
    FEMALE,
    RATHER_NOT_SAY
}
