package com.uhdyl.backend.review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.product.domain.QProduct;
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
        QProduct qProduct = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qReview.user.id.eq(userId));

        List<ReviewResponse> responses = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ReviewResponse.class,
                                qReview.id,
                                qReview.content,
                                qReview.rating,
                                qReview.user.nickname,
                                qReview.imageUrl,
                                qProduct.title,
                                qReview.createdAt
                        )
                )
                .from(qReview)
                .leftJoin(qProduct).on(qReview.productId.eq(qProduct.id))
                .where(builder)
                .orderBy(qReview.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(responses, pageable, total != null ? total : 0);
    }

    @Override
    public Page<ReviewResponse> getAllReviews(Long userId, Pageable pageable) {
        QReview qReview = QReview.review;
        QProduct qProduct = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qReview.targetUserId.eq(userId));

        List<ReviewResponse> responses = jpaQueryFactory
                .select(
                        Projections.constructor(
                        ReviewResponse.class,
                        qReview.id,
                        qReview.content,
                        qReview.rating,
                        qReview.user.nickname,
                        qReview.imageUrl,
                        qProduct.title,
                        qReview.createdAt
                ))
                .from(qReview)
                .leftJoin(qProduct).on(qReview.productId.eq(qProduct.id))
                .where(builder)
                .orderBy(qReview.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(responses, pageable, total != null ? total : 0);
    }
}
