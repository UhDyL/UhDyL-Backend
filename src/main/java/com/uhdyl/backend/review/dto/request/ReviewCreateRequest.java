package com.uhdyl.backend.review.dto.request;

public record ReviewCreateRequest (
        String content,
        Double rating,
        String imageUrl,
        String publicId,
        Long productId
){
}
