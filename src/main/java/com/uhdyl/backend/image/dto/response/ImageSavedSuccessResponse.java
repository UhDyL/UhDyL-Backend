package com.uhdyl.backend.image.dto.response;

public record ImageSavedSuccessResponse(
        String imageUrl,
        String publicId
) {
    public static ImageSavedSuccessResponse to(String url, String publicId){
        return new ImageSavedSuccessResponse(url, publicId);
    }
}
