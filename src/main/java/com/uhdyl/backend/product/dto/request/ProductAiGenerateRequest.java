package com.uhdyl.backend.product.dto.request;

import com.uhdyl.backend.product.domain.Category;
import java.util.List;

public record ProductAiGenerateRequest(
        String condition,
        String weight,
        String quantityPerWeight,
        List<Category> categories,
        List<String> images,
        Long price,
        String tone
) {}