package com.jircik.calorietrackerapi.integration.dto;

import java.util.List;

public record FoodDetailsResponse(
        Food food
) {

    public record Food(
            String food_id,
            String food_name,
            String brand_name,
            Servings servings
    ) {}

    public record Servings(
            List<Serving> serving
    ) {}

    public record Serving(
            String serving_id,
            String serving_description,
            String metric_serving_amount,
            String metric_serving_unit,
            String calories,
            String carbohydrate,
            String protein,
            String fat,
            String is_default
    ) {}
}
