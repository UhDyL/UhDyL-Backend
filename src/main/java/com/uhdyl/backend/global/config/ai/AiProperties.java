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

    @Override
    public String toString() {
        String masked = apiKey == null ? null : mask(apiKey);
        return "AiProperties[baseUrl=" + baseUrl + ", apiKey=" + masked + ", model=" + model + "]";
    }
    private static String mask(String s) {
        int keep = Math.min(4, s.length());
        int stars = Math.max(0, s.length() - keep);
        return "*".repeat(stars) + s.substring(s.length() - keep);
    }
}