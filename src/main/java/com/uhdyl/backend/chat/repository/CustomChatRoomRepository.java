package com.uhdyl.backend.chat.repository;

import com.uhdyl.backend.chat.dto.response.ChatRoomResponse;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import org.springframework.data.domain.Pageable;

public interface CustomChatRoomRepository {
    GlobalPageResponse<ChatRoomResponse> getChatRooms(Long userId, Pageable pageable);
}
