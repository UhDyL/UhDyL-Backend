package com.uhdyl.backend.product.controller;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.product.api.ProductAPI;
import com.uhdyl.backend.product.domain.Category;
import com.uhdyl.backend.product.dto.request.ProductAiGenerateRequest;
import com.uhdyl.backend.product.dto.request.ProductCreateWithAiContentRequest;
import com.uhdyl.backend.product.dto.response.AiGeneratedContentResponse;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductCreateResponse;
import com.uhdyl.backend.product.dto.response.ProductDetailResponse;
import com.uhdyl.backend.product.dto.response.ProductListResponse;
import com.uhdyl.backend.product.dto.response.SalesStatsResponse;
import com.uhdyl.backend.product.service.ProductService;
import jakarta.validation.Valid;
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
     * AI 글 작성 API (1단계 - AI 글 생성)
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @PostMapping("/product/ai-generate")
    public ResponseEntity<ResponseBody<AiGeneratedContentResponse>> generateAiContent(
            Long userId,
            @Valid ProductAiGenerateRequest request) {
        AiGeneratedContentResponse response = productService.generateAiContent(userId, request);
        return ResponseEntity.ok(createSuccessResponse(response));
    }

    /**
     * 상품 등록 API (2단계 - AI로 생성된 글로 등록)
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @PostMapping("/product")
    public ResponseEntity<ResponseBody<ProductCreateResponse>> createProduct(
            Long userId,
            @Valid ProductCreateWithAiContentRequest request) {
        ProductCreateResponse response = productService.createProductWithGeneratedContent(userId, request);
        return ResponseEntity.ok(createSuccessResponse(response));
    }

    /**
     * 상품 삭제 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
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
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
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
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @PatchMapping("/product/{productId}/complete")
    public ResponseEntity<ResponseBody<Void>> completeProduct(
            Long userId,
            @PathVariable Long productId

    ) {
        productService.completeProduct(userId, productId);
        return ResponseEntity.ok(createSuccessResponse());
    }

    /**
     * 카테고리 별 상품 조회 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product/category/{category}")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ProductListResponse>>> getProductsByCategory(
            Long userId,
            @PathVariable Category category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(createSuccessResponse(productService.getProductsByCategory(userId, category, pageable)));
    }

    /**
     * 상품 상세 조회 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseBody<ProductDetailResponse>> getProductDetail(
            Long userId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(createSuccessResponse(productService.getProductDetail(userId, productId)));
    }

    /**
     * 전체 상품 목록 조회 api
     */
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ProductListResponse>>> getAllProducts(
            Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        GlobalPageResponse<ProductListResponse> response = productService.getAllProducts(userId, pageable);
        return ResponseEntity.ok(createSuccessResponse(response));
    }
}
