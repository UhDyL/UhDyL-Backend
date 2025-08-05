package com.uhdyl.backend.review.dto.response;

import com.uhdyl.backend.review.domain.Review;

public record ReviewResponse(
        Long Id,
        String content,
        Long rating,
        String imageUrl
){
    public static ReviewResponse to(Review review){
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getImageUrl()
        );
    }
}