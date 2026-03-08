package com.jircik.calorietrackerapi.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record MealWithFoodsResponse(
        Long mealId,
        LocalDateTime dateTime,
        List<MealFoodResponse> foods
) {
}
