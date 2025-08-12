package com.uhdyl.backend.product.dto.request;

import com.uhdyl.backend.product.domain.Category;
import java.util.List;

public record ProductCreateRequest(
        Category category,
        String breed,
        List<String> images,
        int price,
        String tone
){}
