package com.uhdyl.backend.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.repository.ChatMessageRepository;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.image.domain.ImageType;
import com.uhdyl.backend.image.dto.request.ImageDeleteRequest;
import com.uhdyl.backend.image.dto.response.ImageSavedSuccessResponse;
import com.uhdyl.backend.image.repository.ImageRepository;
import com.uhdyl.backend.review.domain.Review;
import com.uhdyl.backend.review.repository.ReviewRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReviewRepository reviewRepository;

    public ImageSavedSuccessResponse uploadImage(MultipartFile image, String folderPath){

        if(image == null || image.isEmpty())
            throw new BusinessException(ExceptionType.INVALID_IMAGE_FILE);

        if(image.getSize() > 1024 * 1024 * 5)
            throw new BusinessException(ExceptionType.IMAGE_SIZE_EXCEEDED);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    image.getBytes(),
                    Map.of(
                            "folder", folderPath,
                            "public_id", UUID.randomUUID().toString()
                    )
            );

            if(result.get("secure_url") == null || result.get("public_id") == null)
                throw new BusinessException(ExceptionType.IMAGE_UPLOAD_FAILED);

            return ImageSavedSuccessResponse.to(result.get("secure_url").toString(), result.get("public_id").toString());
        } catch (IOException e) {
            throw new BusinessException(ExceptionType.IMAGE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public void deleteImage(List<ImageDeleteRequest> request, Long userId){

        // TODO: 리뷰 도메인 추가 시 로직 추가하기
        request.forEach(imageDeleteRequest -> {
            ImageType imageType = imageDeleteRequest.imageType();
            String publicId = imageDeleteRequest.publicId();
            switch (imageType){
                case USER_IMAGE -> {
                    if(!userRepository.existsByIdAndPublicId(userId, publicId))
                        throw new BusinessException(ExceptionType.IMAGE_ACCESS_DENIED);

                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));
                    user.deleteImage();
                }

                case PRODUCT_IMAGE -> {
                    if (!imageRepository.existsByUserIdAndPublicId(userId, publicId))
                        throw new BusinessException(ExceptionType.IMAGE_ACCESS_DENIED);

                    imageRepository.deleteByPublicId(publicId);
                }

                case CHAT_IMAGE -> {
                    if (!chatMessageRepository.existsByUserIdAndPublicId(userId, publicId))
                        throw new BusinessException(ExceptionType.IMAGE_ACCESS_DENIED);

                    ChatMessage chatMessage = chatMessageRepository.findByPublicId(publicId);
                    chatMessage.deleteImage();
                }

                case REVIEW_IMAGE -> {
                    if(!reviewRepository.existsByUserIdAndPublicId(userId, publicId))
                        throw new BusinessException(ExceptionType.IMAGE_ACCESS_DENIED);

                    Review review = reviewRepository.findByPublicId(publicId);
                    review.deleteImage();
                }

            }

            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new BusinessException(ExceptionType.IMAGE_DELETE_FAILED);
            }
        });

    }

}
