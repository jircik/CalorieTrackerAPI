package com.jircik.calorietrackerapi.domain.dto.response;

import com.jircik.calorietrackerapi.domain.entity.MealTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

public record MealWithFoodsResponse(
        Long mealId,
        LocalDateTime dateTime,
        MealTypeEnum mealType,
        List<MealFoodResponse> foods
) {
}
