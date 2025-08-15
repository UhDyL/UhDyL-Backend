package com.uhdyl.backend.chat.repository;

import com.uhdyl.backend.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, CustomChatRoomRepository {
    Optional<ChatRoom> findByUser1AndUser2AndProductId(Long user1, Long user2, Long productId);

}
