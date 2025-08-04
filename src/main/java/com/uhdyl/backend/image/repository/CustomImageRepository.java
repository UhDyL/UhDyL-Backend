package com.uhdyl.backend.image.repository;

public interface CustomImageRepository {
    boolean existsByUserIdAndPublicId(Long userId, String publicId);
}
