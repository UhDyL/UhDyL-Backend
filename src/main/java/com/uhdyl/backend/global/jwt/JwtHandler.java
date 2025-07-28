package com.uhdyl.backend.global.jwt;


import com.uhdyl.backend.token.entity.RefreshToken;
import com.uhdyl.backend.token.entity.Token;
import com.uhdyl.backend.token.repository.RefreshTokenRepository;
import com.uhdyl.backend.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JwtHandler {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final RefreshTokenRepository refreshTokenRepository;
    public static final String USER_ID = "USER_ID";
    public static final String USER_ROLE = "USER_ROLE";
    private static final String KEY_ROLE = "role";
    private static final long MILLI_SECOND = 1000L;

    public JwtHandler(JwtProperties jwtProperties, RefreshTokenRepository refreshTokenRepository) {
        this.jwtProperties = jwtProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        secretKey = new SecretKeySpec(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Token createTokens(JwtUserClaim jwtUserClaim) {
        Map<String, Object> tokenClaims = this.createClaims(jwtUserClaim);
        Date now = new Date(System.currentTimeMillis());
        long accessTokenExpireIn = jwtProperties.getAccessTokenExpireIn();

        String accessToken = Jwts.builder()
                .claims(tokenClaims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpireIn * MILLI_SECOND))
                .signWith(secretKey)
                .compact();

        String refreshToken = UUID.randomUUID().toString();
        long refreshTokenExpireIn = jwtProperties.getRefreshTokenExpireIn();
        RefreshToken refreshTokenEntity = new RefreshToken(jwtUserClaim.userId(), refreshToken, refreshTokenExpireIn);
        refreshTokenRepository.save(refreshTokenEntity);

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Map<String, Object> createClaims(JwtUserClaim jwtUserClaim) {
        return Map.of(
                USER_ID, jwtUserClaim.userId(),
                USER_ROLE, jwtUserClaim.role()
        );
    }


    // 재발급을 위해 token이 만료되었더라도 claim을 반환하는 메서드
    public Optional<JwtUserClaim> getClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(this.convert(claims));
        } catch (ExpiredJwtException e) {
            Claims claims = e.getClaims();
            return Optional.of(this.convert(claims));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public JwtUserClaim convert(Claims claims) {
        return new JwtUserClaim(
                claims.get(USER_ID, Long.class),
                UserRole.valueOf(claims.get(USER_ROLE, String.class))
        );
    }

    // 필터에서 토큰의 상태를 검증하기 위한 메서드 exception은 사용하는 곳에서 처리
    public JwtUserClaim parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return this.convert(claims);
    }
}
