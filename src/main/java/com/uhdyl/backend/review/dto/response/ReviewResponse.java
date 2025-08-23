package com.uhdyl.backend.review.dto.response;

import com.uhdyl.backend.review.domain.Review;

import java.time.LocalDateTime;

// TODO: 상품 도메인 개발 후 상품의 제목도 함께 전달하기
public record ReviewResponse(
        Long Id,
        String content,
        Long rating,
        String nickName,
        String imageUrl,
        String title,
        LocalDateTime createdAt
){}