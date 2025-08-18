package com.uhdyl.backend.global.config.swagger;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "babzip.swagger")
public class SwaggerProperties {
    private String serverUrl;
    private String description;
}