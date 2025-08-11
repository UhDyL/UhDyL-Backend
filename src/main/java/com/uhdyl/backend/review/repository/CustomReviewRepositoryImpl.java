package com.uhdyl.backend.review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.review.domain.QReview;
import com.uhdyl.backend.review.domain.Review;
import com.uhdyl.backend.review.dto.response.ReviewResponse;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CustomReviewRepositoryImpl implements CustomReviewRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;

    @Override
    public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable) {
        QReview qReview = QReview.review;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qReview.user.id.eq(userId));

        List<Review> reviews = jpaQueryFactory
                .selectFrom(qReview)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .where(builder)
                .fetchOne();

        List<ReviewResponse> response = reviews.stream().map(ReviewResponse::to).toList();

        return new PageImpl<>(response, pageable, total != null ? total : 0);
    }

    @Override
    public Page<ReviewResponse> getAllReviews(Long userId, Pageable pageable) {
        QReview qReview = QReview.review;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qReview.targetUserId.eq(userId));

        List<Review> reviews = jpaQueryFactory
                .selectFrom(qReview)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .where(builder)
                .fetchOne();

        List<ReviewResponse> response = reviews.stream().map(ReviewResponse::to).toList();

        return new PageImpl<>(response, pageable, total != null ? total : 0);
    }

    @Override
    public boolean existsByUserIdAndPublicId(Long userId, String publicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        QReview qReview = QReview.review;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qReview.user.id.eq(userId));
        builder.and(qReview.publicId.eq(publicId));

        return jpaQueryFactory
                .selectFrom(qReview)
                .where(builder)
                .fetchOne() != null;
    }
}
