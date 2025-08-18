package com.uhdyl.backend.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "상품 수정 요청 DTO")
public record ProductUpdateRequest(
        @Schema(description = "수정할 상품 제목", example = "정성으로 키운 제주 하우스 감귤")
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        String title,

        @Schema(description = "수정할 상품 설명", example = "새콤달콤 맛있는 하우스 감귤입니다. 당도 보장!")
        @NotBlank(message = "설명은 필수 입력 항목입니다.")
        String description,

        @Schema(description = "수정할 상품 가격", example = "25000")
        @NotNull(message = "가격은 필수 입력 항목입니다.")
        @Positive(message = "가격은 0보다 커야 합니다.")
        Long price
) {
}