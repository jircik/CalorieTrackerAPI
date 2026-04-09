package com.jircik.calorietrackerapi.domain.dto.response;

import com.jircik.calorietrackerapi.domain.entity.MealTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record MealWithFoodsResponse(

        @Schema(description = "Unique meal ID")
        Long mealId,

        @Schema(description = "Date and time of the meal")
        LocalDateTime dateTime,

        @Schema(description = "Type of meal")
        MealTypeEnum mealType,

        @Schema(description = "List of food entries in this meal")
        List<MealFoodResponse> foods

) {}
