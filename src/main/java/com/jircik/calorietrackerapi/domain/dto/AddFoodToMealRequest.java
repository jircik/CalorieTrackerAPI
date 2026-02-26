package com.jircik.calorietrackerapi.domain.dto;

public record AddFoodToMealRequest(
        String foodName,
        Double quantity,
        String unit

) {

}
