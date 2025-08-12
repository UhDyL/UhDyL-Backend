package com.uhdyl.backend.product.dto.response;

import com.uhdyl.backend.global.response.GlobalPageResponse;

public record MyProductListResponse(
        long totalCount,
        long completedCount,
        GlobalPageResponse<ProductListResponse> products
) {
}
