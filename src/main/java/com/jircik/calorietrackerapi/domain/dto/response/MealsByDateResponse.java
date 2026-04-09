package com.jircik.calorietrackerapi.domain.dto.response;

import com.jircik.calorietrackerapi.domain.entity.MealTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Map;

public record MealsByDateResponse(

        @Schema(description = "ID of the user")
        Long userId,

        @Schema(description = "The requested date")
        LocalDate date,

        @Schema(description = "Meals keyed by meal type. All four keys (BREAKFAST, LUNCH, DINNER, SNACKS) are always present; the value is null if no meal of that type exists for the date")
        Map<MealTypeEnum, MealWithFoodsResponse> meals

) {}
