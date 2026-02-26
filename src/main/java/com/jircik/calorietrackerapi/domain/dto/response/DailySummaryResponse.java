package com.jircik.calorietrackerapi.domain.dto.response;

import java.time.LocalDate;

public record DailySummaryResponse(
        Long userId,
        LocalDate date,
        Double totalCalories,
        Double totalProtein,
        Double totalCarbs,
        Double totalFat,
        Long mealCount,
        Long foodCount
) {
}
