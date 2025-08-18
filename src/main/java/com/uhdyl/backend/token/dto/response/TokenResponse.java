package com.uhdyl.backend.token.dto.response;

import com.uhdyl.backend.token.domain.Token;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
    public static TokenResponse to(Token token){
        return new TokenResponse(token.getAccessToken(), token.getRefreshToken());
    }
}
