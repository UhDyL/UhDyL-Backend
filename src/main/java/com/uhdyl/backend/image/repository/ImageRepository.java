package com.uhdyl.backend.image.repository;

import com.uhdyl.backend.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long>, CustomImageRepository {
    void deleteByPublicId(String publicId);

    Optional<Image> findByImageUrl(String imageUrl);
}
