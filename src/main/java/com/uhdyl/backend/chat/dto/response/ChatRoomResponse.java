package com.uhdyl.backend.chat.dto.response;

import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.ChatRoom;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.product.dto.response.ProductListResponse;

import java.time.LocalDateTime;


public record ChatRoomResponse(
        Long chatRoomId,
        String chatRoomName,
        ProductListResponse product,
        String message,
        LocalDateTime timestamp
) {
    public static ChatRoomResponse to(ChatRoom chatRoom, Product product, ChatMessage chatMessage) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getChatRoomTitle(),
                ProductListResponse.to(product),
                chatMessage == null ? "" : chatMessage.getMessage(),
                chatMessage == null ? chatRoom.getCreatedAt() : chatMessage.getCreatedAt()
        );
    }

}
