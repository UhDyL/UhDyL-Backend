package com.uhdyl.backend.image.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductImageUploadRequest(
        @ArraySchema(
                schema = @Schema(format = "binary"),
                arraySchema = @Schema(description = "상품 이미지들")
        )
        List<MultipartFile> images
) {
}
