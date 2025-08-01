package com.uhdyl.backend.chat.domain;

import com.uhdyl.backend.global.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user1;

    private Long user2;

    @Builder
    public ChatRoom(Long user1, Long user2) {
        this.user1 = user1;
        this.user2 = user2;
    }
}