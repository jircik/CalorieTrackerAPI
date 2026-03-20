package com.jircik.calorietrackerapi.domain.dto.request;

import java.time.LocalDate;

public record GetSummaryRequest(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        String periodType
) {
}
