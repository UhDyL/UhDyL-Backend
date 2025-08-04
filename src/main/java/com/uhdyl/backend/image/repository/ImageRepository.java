package com.uhdyl.backend.image.repository;

import com.uhdyl.backend.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long>, CustomImageRepository {
    void deleteByPublicId(String publicId);
}
