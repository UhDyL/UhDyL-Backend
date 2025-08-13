package com.uhdyl.backend.product.controller;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.product.api.ProductAPI;
import com.uhdyl.backend.product.dto.request.ProductCreateRequest;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductCreateResponse;
import com.uhdyl.backend.product.dto.response.SalesStatsResponse;
import com.uhdyl.backend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductAPI {

    private final ProductService productService;

    /**
     * 상품 등록 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/product")
    public ResponseEntity<ResponseBody<ProductCreateResponse>> createProduct(Long userId, ProductCreateRequest request) {
        ProductCreateResponse response = productService.createProduct(userId, request);
        return ResponseEntity.ok(createSuccessResponse(response));
    }

    /**
     * 상품 삭제 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ResponseBody<Void>> deleteProduct(
            Long userId,
            @PathVariable Long productId
    ) {
        productService.deleteProduct(userId, productId);
        return ResponseEntity.ok(createSuccessResponse());
    }

    /**
     * 판매자 자신이 등록한 상품 조회 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product/me")
    public ResponseEntity<ResponseBody<MyProductListResponse>> getMyProducts(
            Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(createSuccessResponse(productService.getMyProducts(userId, pageable)));
    }

    /**
     * 판매 현황 조회 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product/sales-stats")
    public ResponseEntity<ResponseBody<SalesStatsResponse>> getSalesStats(
            Long userId
    ) {
        return ResponseEntity.ok(createSuccessResponse(productService.getSalesStats(userId)));
    }

    /**
     * 판매 완료 변경 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PatchMapping("/product/{productId}/complete")
    public ResponseEntity<ResponseBody<Void>> completeProduct(
            Long userId,
            @PathVariable Long productId

    ) {
        productService.completeProduct(userId, productId);
        return ResponseEntity.ok(createSuccessResponse());
    }
}
