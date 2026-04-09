package com.jircik.calorietrackerapi.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record SummaryResponse(

        @Schema(description = "ID of the user")
        Long userId,

        @Schema(description = "Start date of the period (inclusive)")
        LocalDate startDate,

        @Schema(description = "End date of the period (inclusive)")
        LocalDate endDate,

        @Schema(description = "Period type used: DAILY, WEEKLY, MONTHLY, or CUSTOM")
        String periodType,

        @Schema(description = "Total calories across all meals in the period")
        Double totalCalories,

        @Schema(description = "Total protein in grams across all meals in the period")
        Double totalProtein,

        @Schema(description = "Total carbohydrates in grams across all meals in the period")
        Double totalCarbs,

        @Schema(description = "Total fat in grams across all meals in the period")
        Double totalFat,

        @Schema(description = "Total number of meals in the period")
        Long mealCount,

        @Schema(description = "Total number of food entries across all meals in the period")
        Long foodCount,

        @Schema(description = "Number of days in the period")
        Integer daysInPeriod,

        @Schema(description = "Average calories per day across the period")
        Double averageCaloriesPerDay

) {}
