package com.uhdyl.backend.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record ReviewImageUploadRequest(
        @Schema(type = "string", format = "binary", description = "리뷰 이미지 파일")
        MultipartFile image
) {
}
