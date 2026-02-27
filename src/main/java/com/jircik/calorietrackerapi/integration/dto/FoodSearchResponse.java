package com.jircik.calorietrackerapi.integration.dto;

import java.util.List;

public record FoodSearchResponse(
        Foods foods
) {
    public record Foods(
            List<Food> food,
            String max_results,
            String page_number,
            String total_results
    ) {}

    public record Food(
            String food_id,
            String food_name,
            String brand_name,
            String food_description,
            String food_type,
            String food_url
    ) {}
}
