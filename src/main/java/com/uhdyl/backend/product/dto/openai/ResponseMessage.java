package com.uhdyl.backend.product.dto.openai;

public record ResponseMessage(
        String role,
        Object content
) {}