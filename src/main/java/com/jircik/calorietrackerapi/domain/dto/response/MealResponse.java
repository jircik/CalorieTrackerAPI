package com.jircik.calorietrackerapi.domain.dto.response;

import com.jircik.calorietrackerapi.domain.entity.MealTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MealResponse(

        @Schema(description = "Unique meal ID")
        Long id,

        @Schema(description = "ID of the user this meal belongs to")
        Long userId,

        @Schema(description = "Date and time of the meal")
        LocalDateTime dateTime,

        @Schema(description = "Type of meal")
        MealTypeEnum mealType,

        @Schema(description = "Timestamp when the meal was created")
        LocalDateTime createdAt

) {}
