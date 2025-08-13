package com.uhdyl.backend.product.dto.response;

public record SalesStatsResponse(
        String name,
        Long salesCount,
        Long salesRevenue
) {
}
