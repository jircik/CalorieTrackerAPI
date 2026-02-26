package com.jircik.calorietrackerapi.domain.dto.request;

import java.time.LocalDateTime;

public record CreateMealRequest(
        Long userId,
        LocalDateTime dateTime) {
}
