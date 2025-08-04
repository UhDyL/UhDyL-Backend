package com.uhdyl.backend.image.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private Long imageOrder;

    private String publicId;

    @Builder
    public Image(String imageUrl, Long imageOrder, String publicId) {
        this.imageUrl = imageUrl;
        this.imageOrder = imageOrder;
        this.publicId = publicId;
    }


}
