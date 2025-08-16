package com.uhdyl.backend.product.dto.request;

import com.uhdyl.backend.product.domain.Category;
import java.util.List;

public record ProductCreateRequest(
        List<Category> categories,
        String breed,
        List<String> images,
        Long price,
        String tone
){}
