package com.uhdyl.backend.product.api;


import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.product.dto.request.ProductCreateRequest;
import com.uhdyl.backend.product.dto.response.MyProductListResponse;
import com.uhdyl.backend.product.dto.response.ProductCreateResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "상품 API", description = "상품 관련 API")
public interface ProductAPI {

    @Operation(
            summary = "상품 등록",
            description = "판매자가 상품을 등록합니다. 제목과 설명은 AI가 생성합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(description = "상품 등록 성공"),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_INPUT),
                    @SwaggerApiFailedResponse(ExceptionType.AI_GENERATION_FAILED)
            }
    )

    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/product")
    ResponseEntity<ResponseBody<ProductCreateResponse>> createProduct(
            @Parameter(hidden = true) Long userId,
            @RequestBody ProductCreateRequest request
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
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @AssignUserId
    public ResponseEntity<ResponseBody<Void>> deleteProduct(
            @Parameter(hidden = true) Long userId,
            @PathVariable Long productId
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
}
