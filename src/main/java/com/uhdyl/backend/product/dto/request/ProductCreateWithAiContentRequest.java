package com.uhdyl.backend.product.dto.request;

import com.uhdyl.backend.product.domain.Category;
import java.util.List;

public record ProductCreateWithAiContentRequest(
        List<Category> categories,
        String breed,
        List<ImageRequest> images,
        Long price,
        String title,
        String description
) {
    public record ImageRequest(
            String url,
            String publicId
    ) {}
}
