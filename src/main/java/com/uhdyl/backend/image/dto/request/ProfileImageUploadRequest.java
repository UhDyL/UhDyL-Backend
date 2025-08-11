package com.uhdyl.backend.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadRequest(
        @Schema(type = "string", format = "binary", description = "프로필 이미지 파일")
        MultipartFile image
) {
}
