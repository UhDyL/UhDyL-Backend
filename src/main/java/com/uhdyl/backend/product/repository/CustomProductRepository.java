package com.uhdyl.backend.product.repository;

import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.product.domain.Category;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductDetailResponse;
import com.uhdyl.backend.product.dto.response.ProductListResponse;
import com.uhdyl.backend.product.dto.response.SalesStatsResponse;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {
    MyProductListResponse getMyProducts(Long userId, Pageable pageable);
    SalesStatsResponse getSalesStats(Long userId);
    ProductDetailResponse getProductDetail(Long userId, Long productId);
    GlobalPageResponse<ProductListResponse> getAllProducts(Pageable pageable);
    GlobalPageResponse<ProductListResponse> searchProducts(String keyword, Category category, Pageable pageable);
}
