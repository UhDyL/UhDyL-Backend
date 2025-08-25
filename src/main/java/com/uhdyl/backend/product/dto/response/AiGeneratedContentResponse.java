package com.uhdyl.backend.product.dto.response;

import com.uhdyl.backend.product.domain.Category;
import java.util.List;

public record AiGeneratedContentResponse(
        String title,
        String description,
        Long price,
        List<String> images,
        List<Category> categories
) {}