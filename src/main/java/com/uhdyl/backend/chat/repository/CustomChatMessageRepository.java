package com.uhdyl.backend.chat.repository;

import com.uhdyl.backend.chat.dto.response.ChatMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CustomChatMessageRepository {
    Page<ChatMessageResponse> findChatMessages(Long roomId, Pageable pageable, LocalDateTime startDateTime);
}
