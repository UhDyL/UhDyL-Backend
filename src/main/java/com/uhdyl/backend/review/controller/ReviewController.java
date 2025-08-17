package com.uhdyl.backend.review.controller;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.review.api.ReviewApi;
import com.uhdyl.backend.review.dto.request.ReviewCreateRequest;
import com.uhdyl.backend.review.dto.response.ReviewResponse;
import com.uhdyl.backend.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성 api
     */
    @PostMapping("/review")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> createReview(
            Long userId,
            @RequestBody ReviewCreateRequest reviewCreateRequest
    )
    {
        reviewService.createReview(userId, reviewCreateRequest);
        return ResponseEntity.ok(createSuccessResponse());
    }

    /**
     *  자신이 작성한 리뷰 페이징 조회 api
     *  구매자가 사용할 api
     */
    @AssignUserId
    @GetMapping("/review/me")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ReviewResponse>>> getMyReviews(
            Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(createSuccessResponse(reviewService.getMyReviews(userId, pageable)));
    }

    /**
     *  특정 판매자에게 작성된 리뷰 페이징 조회 api
     */
    @GetMapping("/review/{nickname}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<GlobalPageResponse<ReviewResponse>>> getAllReviews(
            @PathVariable String nickname,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(createSuccessResponse(reviewService.getAllReviews(nickname, pageable)));
    }

    /**
     * 리뷰 삭제 api
     */
    @DeleteMapping("/review/{reviewId}")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> deleteReview(
            Long userId,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(createSuccessResponse());
    }
}
