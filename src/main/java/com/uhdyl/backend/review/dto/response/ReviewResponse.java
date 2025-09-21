package com.uhdyl.backend.review.dto.response;

import com.uhdyl.backend.review.domain.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long Id,
        String content,
        Double rating,
        String nickName,
        String imageUrl,
        String title,
        LocalDateTime createdAt
){}