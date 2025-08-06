package com.uhdyl.backend.review.domain;

import com.uhdyl.backend.global.base.BaseEntity;
import com.uhdyl.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String imageUrl;

    private String publicId;

    private Long rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long targetUserId;

    @Builder
    public Review(User user, String content, String imageUrl, String publicId, Long rating, Long targetUserId) {
        this.user = user;
        this.content = content;
        this.imageUrl = imageUrl;
        this.publicId = publicId;
        this.rating = rating;
        this.targetUserId = targetUserId;
    }

    public void setUser(User user){
        this.user = user;
    }
}
