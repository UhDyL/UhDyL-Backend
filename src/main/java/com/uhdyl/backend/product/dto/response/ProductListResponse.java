package com.uhdyl.backend.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.uhdyl.backend.product.domain.Product;

public record ProductListResponse(
        Long id,
        String title,
        Long price,
        String sellerName,
        String sellerPicture,
        String mainImageUrl,
        boolean isCompleted,
        Long zzimCount
) {
    @QueryProjection
    public ProductListResponse(Long id, String title, Long price, String sellerName, String sellerPicture, String mainImageUrl, boolean isCompleted, Long zzimCount) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.sellerName = sellerName;
        this.sellerPicture = sellerPicture;
        this.mainImageUrl = mainImageUrl;
        this.isCompleted = isCompleted;
        this.zzimCount = zzimCount;
    }
}