package com.uhdyl.backend.product.dto.response;

import java.util.List;

public record ProductDetailResponse(
        Long id,
        String title,
        Long price,
        String description,
        String sellerName,
        String sellerPicture,
        Double sellerRating,
        Long sellerSalesCount,
        List<String> images,
        boolean isCompleted,
        boolean isZzimed
) {}
