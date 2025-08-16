package com.uhdyl.backend.product.dto.response;

public record SalesStatsResponse(
        String sellerName,
        Long salesCount,
        Long salesRevenue,
        String sellerPicture
) {
}
