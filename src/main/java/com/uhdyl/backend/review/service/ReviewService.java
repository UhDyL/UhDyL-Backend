package com.uhdyl.backend.review.service;

import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.review.domain.Review;
import com.uhdyl.backend.review.dto.request.ReviewCreateRequest;
import com.uhdyl.backend.review.dto.response.ReviewResponse;
import com.uhdyl.backend.review.repository.ReviewRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createReview(Long userId, ReviewCreateRequest request){
        User user = userRepository.findById(userId).
                orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));

        if(!userRepository.existsById(request.targetUserId()))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        if(Objects.equals(user.getId(), request.targetUserId()))
            throw new BusinessException(ExceptionType.CANT_REVIEW_MYSELF);

        Review review = Review.builder()
                .user(user)
                .imageUrl(request.imageUrl())
                .content(request.content())
                .publicId(request.publicId())
                .rating(request.rating())
                .targetUserId(request.targetUserId())
                .build();
        user.addReview(review);
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ExceptionType.REVIEW_NOT_FOUND));

        if (!Objects.equals(review.getUser().getId(), userId)) {
            throw new BusinessException(ExceptionType.CANT_DELETE_REVIEW);
        }
        reviewRepository.deleteById(reviewId);
        review.getUser().deleteReview(review);
    }

    public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return reviewRepository.getMyReviews(userId, pageable);
    }

    public Page<ReviewResponse> getAllReviews(Long userId, Pageable pageable){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return reviewRepository.getAllReviews(userId, pageable);
    }

}
