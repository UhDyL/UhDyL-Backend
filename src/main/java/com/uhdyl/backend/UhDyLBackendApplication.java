package com.uhdyl.backend;

import com.uhdyl.backend.global.oauth.user.KakaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(KakaoProperties.class)
public class UhDyLBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UhDyLBackendApplication.class, args);
    }

}
