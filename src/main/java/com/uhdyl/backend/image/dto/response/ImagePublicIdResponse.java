package com.uhdyl.backend.image.dto.response;

public record ImagePublicIdResponse(
        String publicId
) {
    public static ImagePublicIdResponse to(String publicId){
        return new ImagePublicIdResponse(publicId);
    }
}
