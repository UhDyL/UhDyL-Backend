package com.uhdyl.backend.product.dto.openai;

public record TextContentPart(
        String text
) implements ContentPart {
    @Override
    public String type() { return "text"; }
}