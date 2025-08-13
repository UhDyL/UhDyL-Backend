package com.uhdyl.backend.zzim.dto.response;

public record ZzimResponse(
        Long id,
        String title,
        String imageUrl,
        String price,
        String sellerName
) {}
