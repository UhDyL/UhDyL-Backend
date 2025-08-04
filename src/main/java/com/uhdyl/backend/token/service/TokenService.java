package com.uhdyl.backend.token.service;


import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.jwt.JwtHandler;
import com.uhdyl.backend.global.jwt.JwtUserClaim;
import com.uhdyl.backend.token.dto.response.TokenResponse;
import com.uhdyl.backend.token.domain.RefreshToken;
import com.uhdyl.backend.token.domain.Token;
import com.uhdyl.backend.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtHandler jwtHandler;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse refresh(Token token) {
        JwtUserClaim jwtUserClaim = jwtHandler.getClaims(token.getAccessToken())
                .orElseThrow(() -> new BusinessException(ExceptionType.JWT_INVALID)); // invalid token

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUserId(jwtUserClaim.userId())
                .orElseThrow(() -> new BusinessException(ExceptionType.REFRESH_TOKEN_NOT_EXIST)); // not exist token

        if(!token.getRefreshToken().equals(savedRefreshToken.getRefreshToken())) // userId 비교
            throw new BusinessException(ExceptionType.TOKEN_NOT_MATCHED);

        refreshTokenRepository.deleteByUserId(savedRefreshToken.getUserId());

        Token tokenResponse = jwtHandler.createTokens(jwtUserClaim);
        return new TokenResponse(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
    }


}
