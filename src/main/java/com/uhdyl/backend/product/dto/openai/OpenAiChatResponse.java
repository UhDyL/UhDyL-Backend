package com.uhdyl.backend.product.dto.openai;

import java.util.List;

public record OpenAiChatResponse(
        List<Choice> choices
) {}