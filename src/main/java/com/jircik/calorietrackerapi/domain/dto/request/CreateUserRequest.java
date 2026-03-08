package com.jircik.calorietrackerapi.domain.dto.request;

public record CreateUserRequest(
        String name,
        String email
) {}
