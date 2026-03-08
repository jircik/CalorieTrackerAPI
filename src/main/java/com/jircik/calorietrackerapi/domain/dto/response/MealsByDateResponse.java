package com.jircik.calorietrackerapi.domain.dto.response;

import java.time.LocalDate;
import java.util.List;

public record MealsByDateResponse (
        Long userId,
        LocalDate date,
        List<MealWithFoodsResponse> meals
    ){
}
