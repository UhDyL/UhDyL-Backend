package com.uhdyl.backend.review.api;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.review.dto.request.ReviewCreateRequest;
import com.uhdyl.backend.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "라뷰 API", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(
            summary = "리뷰 작성",
            description = "사용자는 리뷰를 작성합니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "리뷰 작성 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.CANT_REVIEW_MYSELF),
            }
    )
    @PostMapping("/review")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> createReview(
            @Parameter(hidden = true) Long userId,
            @RequestBody ReviewCreateRequest reviewCreateRequest
    );


    @Operation(
            summary = "구매자 본인이 작성한 리뷰 페이징 조회",
            description = "구매자는 자신이 작성한 리뷰를 조회할 수 있습니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    responsePage = ReviewResponse.class,
                    description = "리뷰 페이징 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
            }
    )
    @AssignUserId
    @GetMapping("/review/me")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ReviewResponse>>> getMyReviews(
            @Parameter(hidden = true) Long userId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );


    @Operation(
            summary = "판매자 상점 페이지의 리뷰 페이징 조회",
            description = "구매자는 판매자에게 작성된 리뷰를 조회할 수 있습니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    responsePage = ReviewResponse.class,
                    description = "리뷰 페이징 조회 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
            }
    )
    @GetMapping("/review/{nickname}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ReviewResponse>>> getAllReviews(
            @PathVariable String nickname,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );


    @Operation(
            summary = "리뷰 삭제",
            description = "사용자는 자신이 작성한 리뷰를 삭제할 수 있습니다."
    )
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "리뷰 삭제 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND),
                    @SwaggerApiFailedResponse(ExceptionType.REVIEW_NOT_FOUND),
            }
    )
    @DeleteMapping("/review/{reviewId}")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> deleteReview(
            @Parameter(hidden = true) Long userId,
            @PathVariable Long reviewId
    );
}
