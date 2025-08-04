package com.uhdyl.backend.image.dto.request;

import com.uhdyl.backend.image.domain.ImageType;

public record ImageDeleteRequest(
        ImageType imageType,
        String imageUrl,
        String publicId
) {
}
