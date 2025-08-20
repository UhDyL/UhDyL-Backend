package com.uhdyl.backend.chat.repository;

import com.uhdyl.backend.chat.dto.response.ChatMessageResponse;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CustomChatMessageRepository {
    GlobalPageResponse<ChatMessageResponse> findChatMessages(Long roomId, Pageable pageable, LocalDateTime startDateTime);
    boolean existsByUserIdAndPublicId(Long userId, String publicId);
}
