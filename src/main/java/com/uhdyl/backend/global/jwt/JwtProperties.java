package com.uhdyl.backend.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "uhdyl.jwt")
public class JwtProperties {
    private String secretKey;
    private int accessTokenExpireIn;
    private int refreshTokenExpireIn;
}
