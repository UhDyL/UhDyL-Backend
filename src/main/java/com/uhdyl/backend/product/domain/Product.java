package com.uhdyl.backend.product.domain;

import com.uhdyl.backend.global.base.BaseEntity;
import com.uhdyl.backend.image.domain.Image;
import com.uhdyl.backend.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String title;

    @Lob
    private String description;

    private boolean isSale;

    @NotNull @Positive Long price;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false)
    @OrderBy("imageOrder ASC")
    private List<Image> images = new ArrayList<>();

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Product(Long id, String name, String title, String description, boolean isSale, Long price, Category category, User user) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.isSale = isSale;
        this.price = price;
        this.category = category;
        this.user = user;
    }

    public void addImage(Image image) {
        if (image == null) return;
        this.images.add(image);
    }

    public void removeImage(Image image) {
        if (image == null) return;
        this.images.remove(image);
    }

    public void markSaleCompleted() {
        this.isSale = false;
    }
}
