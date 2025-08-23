package com.uhdyl.backend.global.config.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.openai")
public record AiProperties(String baseUrl, String apiKey, String model) {
}