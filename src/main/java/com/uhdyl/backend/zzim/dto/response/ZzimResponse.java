package com.uhdyl.backend.zzim.dto.response;

public record ZzimResponse(
        Long zzimId,
        Long productId,
        String title,
        String imageUrl,
        Long price,
        String sellerName
) {}
