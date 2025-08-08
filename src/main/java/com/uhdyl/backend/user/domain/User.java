package com.uhdyl.backend.user.domain;

import com.uhdyl.backend.global.base.BaseEntity;
import com.uhdyl.backend.global.oauth.user.OAuth2Provider;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.review.domain.Review;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique = true)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Getter
    private UserRole role;

    private String name;

    private String picture;

    @Enumerated(value = EnumType.STRING)
    private OAuth2Provider provider;

    private String providerId;

    private String publicId;

    @Column(precision = 9, scale = 6)
    private BigDecimal location_x;

    @Column(precision = 9, scale = 6)
    private BigDecimal location_y;
  
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Product> products = new ArrayList<>();

    @Builder
    public User(String email, UserRole role, String name, String picture, OAuth2Provider provider, String providerId) {
        this.email = email;
        this.role = role;
        this.name = name;
        this.picture = picture;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void deleteImage(){
        this.picture = null;
        this.publicId = null;
    }

    public void updateLocation(BigDecimal location_x, BigDecimal location_y){
        this.location_x = location_x;
        this.location_y = location_y;
    }
    public void addReview(Review review){
        this.reviews.add(review);
        review.setUser(this);
    }

    public void deleteReview(Review review){
        this.reviews.remove(review);
        review.setUser(null);
    }
}
