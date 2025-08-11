package com.uhdyl.backend.image.api;

import com.uhdyl.backend.global.aop.AssignUserId;
import com.uhdyl.backend.global.config.swagger.SwaggerApiFailedResponse;
import com.uhdyl.backend.global.config.swagger.SwaggerApiResponses;
import com.uhdyl.backend.global.config.swagger.SwaggerApiSuccessResponse;
import com.uhdyl.backend.global.exception.ExceptionType;
import com.uhdyl.backend.global.response.ResponseBody;
import com.uhdyl.backend.image.dto.request.*;
import com.uhdyl.backend.image.dto.response.ImageSavedSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "이미지 API", description = "이미지 관련 API")
public interface ImageApi {

    @Operation(
            summary = "프로필 이미지 업로드",
            description = "프로필 이미지를 업로드할 수 있습니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProfileImageUploadRequest.class)
                    )
            )
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ImageSavedSuccessResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ImageSavedSuccessResponse.class,
                    description = "프로필 이미지 업로드 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_SIZE_EXCEEDED),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_IMAGE_FILE),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_UPLOAD_FAILED)
            }
    )
    @PostMapping(value = "/image/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ImageSavedSuccessResponse>> uploadProfileImage(
            @Parameter(hidden = true) Long userId,
            @RequestParam MultipartFile image
    );

    @Operation(
            summary = "채팅 이미지 업로드",
            description = "채팅 이미지를 업로드할 수 있습니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ChatImageUploadRequest.class)
                    )
            )
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ImageSavedSuccessResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ImageSavedSuccessResponse.class,
                    description = "채팅 이미지 업로드 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_SIZE_EXCEEDED),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_IMAGE_FILE),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_UPLOAD_FAILED)
            }
    )
    @PostMapping(value="/image/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ImageSavedSuccessResponse>> uploadChatMessageImage(
            @RequestParam Long roomId,
            @RequestParam MultipartFile image
    );


    @Operation(
            summary = "상품 이미지 업로드",
            description = "상품 이미지를 업로드할 수 있습니다.",

            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProductImageUploadRequest.class)
                    )
            )
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ImageSavedSuccessResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ImageSavedSuccessResponse.class,
                    description = "상품 이미지 업로드 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_SIZE_EXCEEDED),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_IMAGE_FILE),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_UPLOAD_FAILED)
            }
    )
    @PostMapping(value="/image/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<List<ImageSavedSuccessResponse>>> uploadProductImage(
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("images") List<MultipartFile> images
    );

    @Operation(
            summary = "리뷰 이미지 업로드",
            description = "리뷰 이미지를 업로드할 수 있습니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ReviewImageUploadRequest.class)
                    )
            )
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ImageSavedSuccessResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    response = ImageSavedSuccessResponse.class,
                    description = "리뷰 이미지 업로드 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_SIZE_EXCEEDED),
                    @SwaggerApiFailedResponse(ExceptionType.INVALID_IMAGE_FILE),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_UPLOAD_FAILED)
            }
    )
    @PostMapping("/image/review")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<ImageSavedSuccessResponse>> uploadReviewImage(
            @Parameter(hidden = true) Long userId,
            @RequestParam MultipartFile image
    );

    @Operation(
            summary = "이미지 삭제",
            description = "이전에 업로드한 이미지를 삭제할 수 있습니다."
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ImageSavedSuccessResponse.class)))
    @SwaggerApiResponses(
            success = @SwaggerApiSuccessResponse(
                    description = "이미지 삭제 성공"
            ),
            errors = {
                    @SwaggerApiFailedResponse(ExceptionType.NEED_AUTHORIZED),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_ACCESS_DENIED),
                    @SwaggerApiFailedResponse(ExceptionType.IMAGE_DELETE_FAILED),
                    @SwaggerApiFailedResponse(ExceptionType.USER_NOT_FOUND)
            }
    )
    @DeleteMapping("/image")
    @AssignUserId
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ResponseBody<Void>> deleteImage(
            @RequestBody List<ImageDeleteRequest> request,
            @Parameter(hidden = true) Long userId
    );
}
