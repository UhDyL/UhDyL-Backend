package com.uhdyl.backend.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.uhdyl.backend.chat.domain.ChatMessage;
import com.uhdyl.backend.chat.repository.ChatMessageRepository;
import com.uhdyl.backend.global.exception.BusinessException;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.image.domain.ImageType;
import com.uhdyl.backend.image.dto.response.ImageSavedSuccessResponse;
import com.uhdyl.backend.image.repository.ImageRepository;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ImageSavedSuccessResponse uploadImage(MultipartFile image, String folderPath){
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    image.getBytes(),
                    Map.of(
                            "folder", folderPath,
                            "public_id", UUID.randomUUID().toString()
                    )
            );
            return ImageSavedSuccessResponse.to(result.get("secure_url").toString(), result.get("public_id").toString());
        } catch (IOException e) {
            throw new BusinessException(ExceptionType.IMAGE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public void deleteImage(ImageType imageType, String publicId, Long userId){

        // TODO: 리뷰 도메인 추가 시 로직 추가하기
        switch (imageType){
            case USER_IMAGE -> {
                if(userRepository.existsByIdAndPublicId(userId, publicId)){
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ExceptionType.USER_NOT_FOUND));
                    user.deleteImage();
                }
            }

            case PRODUCT_IMAGE -> {
                if (imageRepository.existsByUserIdAndPublicId(userId, publicId)){
                    imageRepository.deleteByPublicId(publicId);
                }
            }

            case CHAT_IMAGE -> {
                if (chatMessageRepository.existsByUserIdAndPublicId(userId, publicId)){
                    ChatMessage chatMessage = chatMessageRepository.findByPublicId(publicId);
                    chatMessage.deleteImage();
                }
            }
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new BusinessException(ExceptionType.IMAGE_DELETE_FAILED);
        }


    }

}
