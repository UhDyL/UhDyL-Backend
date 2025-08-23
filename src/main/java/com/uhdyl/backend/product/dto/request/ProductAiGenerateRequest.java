package com.uhdyl.backend.product.dto.request;

import com.uhdyl.backend.product.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProductAiGenerateRequest(

        @NotBlank(message = "상품 상태 정보는 비워둘 수 없습니다.")
        String condition,

        @NotBlank(message = "무게당 가격 정보는 비워둘 수 없습니다.")
        String pricePerWeight,
        @NotEmpty(message = "카테고리는 최소 하나 이상 선택해야 합니다.")
        List<Category> categories,

        @NotEmpty(message = "이미지는 최소 하나 이상 첨부되어야 합니다.")
        List<String> images,
        @NotNull(message = "가격은 필수입니다.")
        Long price,
        @NotBlank(message = "글의 톤앤매너는 비워둘 수 없습니다.")
        String tone
) {}