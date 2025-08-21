package com.uhdyl.backend.review.service;

import com.uhdyl.backend.chat.domain.ChatRoom;
import com.uhdyl.backend.chat.repository.ChatRoomRepository;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.GlobalPageResponse;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.product.repository.ProductRepository;
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
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void createReview(Long userId, ReviewCreateRequest request){
        User user = userRepository.findById(userId).
                orElseThrow(()->new BusinessException(ExceptionType.USER_NOT_FOUND));


        Product product = productRepository.findById(request.productId())
                .orElseThrow(()->new BusinessException(ExceptionType.PRODUCT_NOT_FOUND));
        Long targetUserId = product.getUser().getId();
        Long user1 = Math.min(userId, targetUserId);
        Long user2 = Math.max(userId, targetUserId);

        if(Objects.equals(user.getId(), targetUserId))
            throw new BusinessException(ExceptionType.CANT_REVIEW_MYSELF);

        ChatRoom chatRoom = chatRoomRepository.findByUser1AndUser2AndProductId(user1, user2, request.productId())
                .orElseThrow(()->new BusinessException(ExceptionType.CHATROOM_NOT_EXIST));

        if(!chatRoom.isTradeCompleted())
            throw new BusinessException(ExceptionType.CANT_REVIEW_FAKE);

        if(!userRepository.existsById(targetUserId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        // TODO: userId와 productId 인덱스 만들지 생각해보기
        //  unique 제약 조건 추가하기
        if(reviewRepository.existsByUser_IdAndProductId(userId, request.productId()))
            throw new BusinessException(ExceptionType.CANT_REVIEW_MORE);

        Review review = Review.builder()
                .user(user)
                .imageUrl(request.imageUrl())
                .content(request.content())
                .publicId(request.publicId())
                .rating(request.rating())
                .targetUserId(targetUserId)
                .productId(request.productId())
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

    public GlobalPageResponse<ReviewResponse> getMyReviews(Long userId, Pageable pageable){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        return GlobalPageResponse.create(reviewRepository.getMyReviews(userId, pageable));
    }

    public GlobalPageResponse<ReviewResponse> getAllReviews(String nickName, Pageable pageable){
        User user = userRepository.findByNickname(nickName)
                .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));

        return GlobalPageResponse.create(reviewRepository.getAllReviews(user.getId(), pageable));
    }

}
