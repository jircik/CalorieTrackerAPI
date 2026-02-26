package com.jircik.calorietrackerapi.domain.dto.request;

public record AddFoodToMealRequest(
        String foodName,
        Double quantity,
        String unit

) {

}
