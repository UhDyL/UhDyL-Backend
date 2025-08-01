package com.uhdyl.backend.chat.dto.response;

import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.ChatRoom;

import java.util.List;

public record ChatRoomResponse(
        Long chatRoomId,
        Long userId,
        Long sellerId,
        List<ChatMessageResponse> message
) {
    public static ChatRoomResponse to(ChatRoom chatRoom, List<ChatMessage> message) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getUser1(),
                chatRoom.getUser2(),
                message.stream().map(ChatMessageResponse::to).toList()
        );
    }

}
