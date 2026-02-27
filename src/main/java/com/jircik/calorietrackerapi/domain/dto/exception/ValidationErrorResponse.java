package com.jircik.calorietrackerapi.domain.dto.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        List<String> errors,
        String path,
        LocalDateTime timestamp
) {}
