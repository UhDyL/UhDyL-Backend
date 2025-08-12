package com.uhdyl.backend.product.repository;

import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {
    MyProductListResponse getMyProducts(Long userId, Pageable pageable);
}
