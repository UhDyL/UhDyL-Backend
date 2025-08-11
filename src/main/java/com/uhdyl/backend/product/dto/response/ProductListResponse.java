package com.uhdyl.backend.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record ProductListResponse(
        Long id,
        String name,
        String price,
        String sellerName,
        String mainImageUrl,
        boolean isCompleted
) {
    @QueryProjection
    public ProductListResponse{}
}