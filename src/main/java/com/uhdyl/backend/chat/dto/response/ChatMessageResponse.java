package com.uhdyl.backend.chat.dto.response;

import com.uhdyl.backend.chat.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse (
        String message,
        Long senderId,
        String senderName,
        String senderImage,
        LocalDateTime timestamp
){
    public static ChatMessageResponse to(ChatMessage message) {
        return new ChatMessageResponse(message.getMessage(), message.getUser().getId(), message.getUser().getName(), message.getImageUrl(), message.getCreatedAt());
    }

    public static List<ChatMessageResponse> to(List<ChatMessage> message) {
        return message.stream().map(ChatMessageResponse::to).toList();
    }
}
