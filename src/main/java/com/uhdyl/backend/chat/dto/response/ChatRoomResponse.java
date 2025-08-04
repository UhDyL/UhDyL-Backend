package com.uhdyl.backend.chat.dto.response;

import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.ChatRoom;

import java.util.List;

public record ChatRoomResponse(
        Long chatRoomId,
        Long userId,
        Long sellerId
) {
    public static ChatRoomResponse to(ChatRoom chatRoom) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getUser1(),
                chatRoom.getUser2()
        );
    }

}
