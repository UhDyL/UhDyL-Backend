package com.uhdyl.backend.product.dto.openai;

import java.util.List;

public record Message(
        String role,
        List<ContentPart> content
) {
    // 텍스트만 있는 간단한 메시지를 위한 생성자
    public static Message of(String role, String text) {
        return new Message(role, List.of(new TextContentPart(text)));
    }
}