package com.uhdyl.backend.product.dto.request;

import com.uhdyl.backend.product.domain.Category;
import java.util.List;

public record ProductCreateWithAiContentRequest(
        List<Category> categories,
        String breed,
        List<String> images,
        Long price,
        String title,
        String description
) {}
