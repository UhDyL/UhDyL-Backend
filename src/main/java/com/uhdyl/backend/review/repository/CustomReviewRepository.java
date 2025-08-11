package com.uhdyl.backend.review.repository;

import com.uhdyl.backend.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReviewRepository {
    Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable);
    Page<ReviewResponse> getAllReviews(Long userId, Pageable pageable);
    boolean existsByUserIdAndPublicId(Long userId, String publicId);
}
