package com.uhdyl.backend.token.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
