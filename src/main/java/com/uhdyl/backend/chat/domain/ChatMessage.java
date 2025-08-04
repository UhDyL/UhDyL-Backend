package com.uhdyl.backend.chat.domain;

import com.uhdyl.backend.global.base.BaseEntity;
import com.uhdyl.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 메시지를 보낸 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String message;

    private String imageUrl;

    private String publicId;

    @Builder
    public ChatMessage(ChatRoom chatRoom, User user, String message, String imageUrl, String publicId) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.message = message;
        this.imageUrl = imageUrl;
        this.publicId = publicId;
    }

    public void deleteImage(){
        this.imageUrl = null;
        this.publicId = null;
    }
}
