package com.uhdyl.backend.image.domain;

import lombok.Getter;

@Getter
public enum ImageType {
    USER_IMAGE("user-image", "사용자 프로필 이미지"),
    PRODUCT_IMAGE("product-image", "상품 이미지"),
    CHAT_IMAGE("chat-image", "채팅 이미지"),
    REVIEW_IMAGE("review-image", "리뷰 이미지");

    private final String key;
    private final String description;

    ImageType(String key, String description) {
        this.key = key;
        this.description = description;
    }
}


