package com.jircik.calorietrackerapi.domain.dto;

import java.time.LocalDateTime;

public record CreateMealRequest(
        Long userId,
        LocalDateTime dateTime) {
}
