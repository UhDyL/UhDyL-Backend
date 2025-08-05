package com.uhdyl.backend.review.dto.request;

public record ReviewCreateRequest (
        String content,
        Long rating,
        String imageUrl,
        String publicId,
        Long targetUserId
){
}
