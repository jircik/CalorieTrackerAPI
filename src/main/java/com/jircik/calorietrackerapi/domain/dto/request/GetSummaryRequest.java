package com.jircik.calorietrackerapi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record GetSummaryRequest(

        @Schema(description = "ID of the user", example = "1")
        Long userId,

        @Schema(description = "Start date of the period (ISO 8601 date)", example = "2026-04-01")
        LocalDate startDate,

        @Schema(description = "End date of the period; required for CUSTOM, auto-derived for WEEKLY/MONTHLY", example = "2026-04-07")
        LocalDate endDate,

        @Schema(description = "Period type: DAILY, WEEKLY, MONTHLY, or CUSTOM", example = "WEEKLY")
        String periodType

) {}
