package com.uhdyl.backend.image.controller;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.image.api.ImageApi;
import com.uhdyl.backend.image.domain.ImageType;
import com.uhdyl.backend.image.dto.request.ImageDeleteRequest;
import com.uhdyl.backend.image.dto.response.ImagePublicIdResponse;
import com.uhdyl.backend.image.dto.response.ImageSavedSuccessResponse;
import com.uhdyl.backend.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.uhdyl.backend.global.response.ResponseUtil.createSuccessResponse;

@RestController
@RequiredArgsConstructor
public class ImageController implements ImageApi {

    private final ImageService imageService;

    /**
     * 프로필 이미지 업로드 api
     */
    @PostMapping("/image/user")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ImageSavedSuccessResponse>> uploadProfileImage(
            Long userId,
            @RequestParam MultipartFile image
    ){
        return ResponseEntity.ok(createSuccessResponse(
                imageService.uploadImage(image, "profile/" + userId + "/")
        ));
    }

    /**
     * 채팅 이미지 업로드 api
     */
    @PostMapping("/image/chat")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ImageSavedSuccessResponse>> uploadChatMessageImage(
            @RequestParam Long roomId,
            @RequestParam MultipartFile image
    ){
        return ResponseEntity.ok(createSuccessResponse(
                imageService.uploadImage(image, "chat/" + roomId + "/")
        ));
    }

    /**
     * 상품 이미지 업로드 api
     */
    @PostMapping("/image/product")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<List<ImageSavedSuccessResponse>>> uploadProductImage(
            @RequestParam List<MultipartFile> images
    ){

        List<ImageSavedSuccessResponse> response = new ArrayList<>();
        for(int i = 0; i < images.size(); i++){
            response.add(
                    imageService.uploadImage(images.get(i), "product/" + (i + 1) + "/")
            );
        }

        return ResponseEntity.ok(createSuccessResponse(response));
    }

    @PostMapping("/image/review")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ImageSavedSuccessResponse>> uploadReviewImage(
            Long userId,
            @RequestParam MultipartFile image
    ){
        return ResponseEntity.ok(createSuccessResponse(
                imageService.uploadImage(image, "review/" + userId + "/")
        ));
    }
    /**
     * 이미지 삭제 api
     */
    @DeleteMapping("/image")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> deleteImage(
        @RequestBody List<ImageDeleteRequest> request,
        Long userId
    ){
        imageService.deleteImage(request, userId);
        return ResponseEntity.ok(createSuccessResponse());
    }

    @GetMapping("/image/publicId")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ImagePublicIdResponse>> getPublicId(
            Long userId,
            @RequestParam String imageUrl,
            @RequestParam ImageType imageType
    ){
        return ResponseEntity.ok(createSuccessResponse(imageService.getPublicId(userId, imageUrl, imageType)));
    }

}
