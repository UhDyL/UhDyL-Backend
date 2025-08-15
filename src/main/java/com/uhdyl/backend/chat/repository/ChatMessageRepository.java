package com.uhdyl.backend.chat.repository;

import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, CustomChatMessageRepository{
    List<ChatMessage> findMessagesWithUserByChatRoom(ChatRoom chatRoom);

    ChatMessage findByPublicId(String publicId);

    Optional<ChatMessage> findFirstByChatRoom_IdOrderByCreatedAtDescIdDesc(Long chatRoomId);
}
