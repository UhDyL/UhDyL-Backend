package com.uhdyl.backend.product.dto.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ContentPart {
    @JsonProperty("type")
    String type();
}