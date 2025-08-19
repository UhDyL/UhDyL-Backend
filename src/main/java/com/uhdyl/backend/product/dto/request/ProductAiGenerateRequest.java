package com.uhdyl.backend.product.dto.request;

import com.uhdyl.backend.product.domain.Category;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@NotBlank
public record ProductAiGenerateRequest(
        String condition,
        String pricePerWeight,
        List<Category> categories,
        List<String> images,
        Long price,
        String tone
) {}