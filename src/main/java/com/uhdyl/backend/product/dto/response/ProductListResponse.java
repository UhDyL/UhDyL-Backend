package com.uhdyl.backend.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.uhdyl.backend.product.domain.Product;

public record ProductListResponse(
        Long id,
        String name,
        String title,
        Long price,
        String sellerName,
        String sellerPicture,
        String mainImageUrl,
        boolean isCompleted
) {
    @QueryProjection
    public ProductListResponse(Long id, String name, String title, Long price, String sellerName, String sellerPicture, String mainImageUrl, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.price = price;
        this.sellerName = sellerName;
        this.sellerPicture = sellerPicture;
        this.mainImageUrl = mainImageUrl;
        this.isCompleted = isCompleted;
    }

    public static ProductListResponse to(Product product){
        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getTitle(),
                product.getPrice(),
                product.getUser().getNickname(),
                product.getUser().getPicture(),
                product.getImages().get(0).getImageUrl(),
                product.isSale()
        );
    }
}