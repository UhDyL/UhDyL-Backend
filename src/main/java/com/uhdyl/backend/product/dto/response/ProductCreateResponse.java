package com.uhdyl.backend.product.dto.response;

import java.util.List;

public record ProductCreateResponse(
        Long id,
        String title,
        String description,
        Long price,
        List<String> images,
        boolean isSale
) {}
