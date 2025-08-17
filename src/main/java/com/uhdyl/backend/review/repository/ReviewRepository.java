package com.uhdyl.backend.review.repository;

import com.uhdyl.backend.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {
    Review findByPublicId(String publicId);
    boolean existsByUser_IdAndPublicId(Long userId, String publicId);
}
