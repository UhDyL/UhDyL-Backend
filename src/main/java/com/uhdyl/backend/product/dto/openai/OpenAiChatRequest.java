package com.uhdyl.backend.product.dto.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenAiChatRequest(
        String model,
        List<Message> messages,
        @JsonProperty("response_format") ResponseFormat responseFormat,
        @JsonProperty("max_tokens") Integer maxTokens
) {
    public OpenAiChatRequest(String model, List<Message> messages) {
        this(model, messages, new ResponseFormat("json_object"), 400);
    }
}