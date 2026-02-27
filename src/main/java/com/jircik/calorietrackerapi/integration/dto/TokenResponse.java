package com.jircik.calorietrackerapi.integration.dto;

public record TokenResponse(
        String access_token,
        String token_type,
        Integer expires_in,
        String scope
) {}