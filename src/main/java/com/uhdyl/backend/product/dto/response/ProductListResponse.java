package com.uhdyl.backend.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record ProductListResponse(
        Long id,
        String name,
        Long price,
        String sellerName,
        String mainImageUrl,
        boolean isCompleted
) {
    @QueryProjection
    public ProductListResponse(Long id, String name, Long price, String sellerName, String mainImageUrl, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sellerName = sellerName;
        this.mainImageUrl = mainImageUrl;
        this.isCompleted = isCompleted;
    }
}