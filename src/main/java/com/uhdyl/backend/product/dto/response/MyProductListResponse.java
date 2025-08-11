package com.uhdyl.backend.product.dto.response;

import org.springframework.data.domain.Page;

public record MyProductListResponse(
        long totalCount,
        long completedCount,
        Page<ProductListResponse> products
) {
}
