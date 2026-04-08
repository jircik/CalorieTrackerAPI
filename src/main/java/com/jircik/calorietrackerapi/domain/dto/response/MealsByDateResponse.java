package com.jircik.calorietrackerapi.domain.dto.response;

import com.jircik.calorietrackerapi.domain.entity.MealTypeEnum;

import java.time.LocalDate;
import java.util.Map;


public record MealsByDateResponse (
        Long userId,
        LocalDate date,
        Map<MealTypeEnum, MealWithFoodsResponse> meals
    ){
}
