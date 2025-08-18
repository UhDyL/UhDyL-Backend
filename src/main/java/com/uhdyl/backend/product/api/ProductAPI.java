package com.uhdyl.backend.product.api;


import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.product.domain.Category;
import com.uhdyl.backend.product.dto.request.ProductAiGenerateRequest;
import com.uhdyl.backend.product.dto.request.ProductCreateWithAiContentRequest;
import com.uhdyl.backend.product.dto.response.AiGeneratedContentResponse;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductCreateResponse;
import com.uhdyl.backend.product.dto.response.ProductDetailResponse;
import com.uhdyl.backend.product.dto.response.ProductListResponse;
import com.uhdyl.backend.product.dto.response.SalesStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "상품 API", description = "상품 관련 API")
public interface ProductAPI {

    @Operation(
            summary = "AI 글 작성",
            description = "AI가 상품 정보를 바탕으로 제목과 설명을 생성합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "AI 글 생성 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_INPUT),
                    @SwaggerApiFailedResponse(ExceptionType.AI_GENERATION_FAILED)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @PostMapping("/product/ai-generate")
    ResponseEntity<ResponseBody<AiGeneratedContentResponse>> generateAiContent(
            @Parameter(hidden = true) Long userId,
            @RequestBody ProductAiGenerateRequest request
    );

    @Operation(
            summary = "상품 등록",
            description = "AI로 생성된 글을 바탕으로 상품을 등록합니다. 제목과 설명은 사용자가 수정할 수 있습니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "상품 등록 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_INPUT),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FARMER)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @PostMapping("/product")
    ResponseEntity<ResponseBody<ProductCreateResponse>> createProduct(
            @Parameter(hidden = true) Long userId,
            @RequestBody ProductCreateWithAiContentRequest request
    );

    @Operation(
            summary = "상품 삭제",
            description = "판매자가 본인의 상품을 삭제합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "상품 삭제 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.PRODUCT_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.CANT_DELETE_PRODUCT)
            }
    )

    @DeleteMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @AssignUserId
    public ResponseEntity<ResponseBody<Void>> deleteProduct(
            @Parameter(hidden = true) Long userId,
            @PathVariable Long productId
    );

    @Operation(
            summary = "상품 목록",
            description = "전체 상품 목록을 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "전체 상품 목록 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_INPUT),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product")
    ResponseEntity<ResponseBody<GlobalPageResponse<ProductListResponse>>> getAllProducts(
            @Parameter(hidden = true) Long userId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(
            summary = "상품 목록",
            description = "판매자가 등록한 상품 목록 및 판매 현황을 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "상품 목록 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_INPUT),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product/me")
    ResponseEntity<ResponseBody<MyProductListResponse>> getMyProducts(
            @Parameter(hidden = true) Long userId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(
            summary = "판매자 판매 현황 조회",
            description = "판매자의 판매 건수와 판매 수익을 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "판매 현황 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @GetMapping("/product/sales-stats")
    ResponseEntity<ResponseBody<SalesStatsResponse>> getSalesStats(
            @Parameter(hidden = true) Long userId
    );

    @Operation(
            summary = "상품 판매 완료 처리",
            description = "판매자가 본인의 상품을 판매 완료로 상태를 변경합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "상품 판매 완료 처리 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.PRODUCT_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.CANT_UPDATE_PRODUCT),
                    @SwaggerApiFailedResponse(ExceptionType.PRODUCT_COMPLETE_CONFLICT)
            }
    )
    @PatchMapping("/product/{productId}/complete")
    @PreAuthorize("isAuthenticated() and hasRole('FARMER')")
    @AssignUserId
    ResponseEntity<ResponseBody<Void>> completeProduct(
            @Parameter(hidden = true) Long userId,
            @PathVariable Long productId
    );

    @Operation(
            summary = "카테고리별 상품 조회",
            description = "특정 카테고리에 속하는 상품 목록을 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "카테고리별 상품 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_INPUT),
                    @SwaggerApiFailedResponse(ExceptionType.CATEGORY_NOT_FOUND)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product/category/{category}")
    ResponseEntity<ResponseBody<GlobalPageResponse<ProductListResponse>>> getProductsByCategory(
            @Parameter(hidden = true) Long userId,
            @PathVariable Category category,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(
            summary = "상품 (게시글) 상세보기",
            description = "특정 상품의 상세 정보를 조회합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "상품 상세 조회 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_INPUT),
                    @SwaggerApiFailedResponse(ExceptionType.PRODUCT_NOT_FOUND)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/product/{productId}")
    ResponseEntity<ResponseBody<ProductDetailResponse>> getProductDetail(
            @Parameter(hidden = true) Long userId,
            @PathVariable Long productId
    );
}
