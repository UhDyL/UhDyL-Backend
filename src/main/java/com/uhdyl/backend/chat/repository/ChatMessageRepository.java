package com.uhdyl.backend.chat.repository;

import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, CustomChatMessageRepository{
    List<ChatMessage> findMessagesWithUserByChatRoom(ChatRoom chatRoom);

}
