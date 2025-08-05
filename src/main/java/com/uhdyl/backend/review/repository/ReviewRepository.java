package com.uhdyl.backend.review.repository;

import com.uhdyl.backend.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {
}
