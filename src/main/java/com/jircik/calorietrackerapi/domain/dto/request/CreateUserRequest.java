package com.jircik.calorietrackerapi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserRequest(

        @Schema(description = "The user's full name", example = "Arthur Jircik")
        String name,

        @Schema(description = "The user's email address", example = "arthur@example.com")
        String email

) {}
