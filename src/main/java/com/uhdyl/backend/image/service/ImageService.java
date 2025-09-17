package com.uhdyl.backend.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.repository.ChatMessageRepository;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.image.domain.Image;
import com.uhdyl.backend.image.domain.ImageType;
import com.uhdyl.backend.image.dto.request.ImageDeleteRequest;
import com.uhdyl.backend.image.dto.response.ImagePublicIdResponse;
import com.uhdyl.backend.image.dto.response.ImageSavedSuccessResponse;
import com.uhdyl.backend.image.repository.ImageRepository;
import com.uhdyl.backend.product.repository.ProductRepository;
import com.uhdyl.backend.review.domain.Review;
import com.uhdyl.backend.review.repository.ReviewRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    public List<ImageSavedSuccessResponse> uploadImage(MultipartFile[] images, String folderPath){

        List<String> uploadedImagesPublicId = new ArrayList<>();
        List<ImageSavedSuccessResponse> responses = new ArrayList<>();

        try {
            for (MultipartFile image : images) {

                if (image == null || image.isEmpty())
                    throw new BusinessException(ExceptionType.INVALID_IMAGE_FILE);

                if (image.getSize() > 1024 * 1024 * 5)
                    throw new BusinessException(ExceptionType.IMAGE_SIZE_EXCEEDED);

                String contentType = image.getContentType();
                if (!ALLOWED_CONTENT_TYPES.contains(contentType))
                    throw new BusinessException(ExceptionType.INVALID_IMAGE_FILE);

                Map<?, ?> result = cloudinary.uploader().upload(
                        image.getBytes(),
                        Map.of(
                                "folder", folderPath,
                                "public_id", UUID.randomUUID().toString()
                        )
                );


                if (result.get("secure_url") == null || result.get("public_id") == null)
                    throw new BusinessException(ExceptionType.IMAGE_UPLOAD_FAILED);

                uploadedImagesPublicId.add(result.get("public_id").toString());
                responses.add(ImageSavedSuccessResponse.to(result.get("secure_url").toString(), result.get("public_id").toString()));

            }
        }
        catch (BusinessException e) {
            rollbackImage(uploadedImagesPublicId);
            throw e;
        }
        catch (Exception e) {
            rollbackImage(uploadedImagesPublicId);
            throw new BusinessException(ExceptionType.IMAGE_UPLOAD_FAILED);
        }
        return responses;
    }

    @Transactional
    public void deleteImage(List<ImageDeleteRequest> request, Long userId){

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
                    if (!chatMessageRepository.existsByUser_IdAndPublicId(userId, publicId))
                        throw new BusinessException(ExceptionType.IMAGE_ACCESS_DENIED);

                    ChatMessage chatMessage = chatMessageRepository.findByPublicId(publicId);
                    chatMessage.deleteImage();
                }

                case REVIEW_IMAGE -> {
                    if(!reviewRepository.existsByUser_IdAndPublicId(userId, publicId))
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

    public ImagePublicIdResponse getPublicId(Long userId, String imageUrl, ImageType imageType){
        if(!userRepository.existsById(userId))
            throw new BusinessException(ExceptionType.USER_NOT_FOUND);

        switch (imageType){
            case USER_IMAGE -> {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));
                return ImagePublicIdResponse.to(user.getPublicId());
            }

            case PRODUCT_IMAGE -> {
                Image image = imageRepository.findByImageUrl(imageUrl)
                        .orElseThrow(() -> new BusinessException(ExceptionType.IMAGE_ACCESS_DENIED));
                return ImagePublicIdResponse.to(image.getPublicId());
            }

            case CHAT_IMAGE -> {
                ChatMessage chatMessage = chatMessageRepository.findByUser_IdAndImageUrl(userId, imageUrl)
                        .orElseThrow(() -> new BusinessException(ExceptionType.IMAGE_ACCESS_DENIED));
                return ImagePublicIdResponse.to(chatMessage.getPublicId());
            }

            case REVIEW_IMAGE -> {
                Review review = reviewRepository.findByUser_IdAndImageUrl(userId, imageUrl)
                        .orElseThrow(() -> new BusinessException(ExceptionType.REVIEW_NOT_FOUND));
                return ImagePublicIdResponse.to(review.getPublicId());
            }
        }
        return ImagePublicIdResponse.to("이미지가 존재하지 않습니다.");
    }

    public void rollbackImage(List<String> publicIds){
        try{
            for (String publicId : publicIds) {
                try {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException e) {
                    log.warn("이미지 롤백에 실패했습니다. publicId : {}", publicId);
                }
            }
        }
        catch (Exception e){
            log.warn("이미지 롤백이 일부 실패했습니다.");
        }
    }
}
