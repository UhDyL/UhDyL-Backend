package com.uhdyl.backend.global.config.ai;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ai.openai")
@Validated
public record AiProperties(
        @NotBlank String baseUrl,
        @NotBlank String apiKey,
        @NotBlank String model) {

    public AiProperties {
        if (baseUrl != null && baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
    }
}