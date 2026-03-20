package com.jircik.calorietrackerapi.domain.dto.response;

import java.time.LocalDate;

public record SummaryResponse(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        String periodType,
        Double totalCalories,
        Double totalProtein,
        Double totalCarbs,
        Double totalFat,
        Long mealCount,
        Long foodCount,
        Integer daysInPeriod,
        Double averageCaloriesPerDay
) {
}
