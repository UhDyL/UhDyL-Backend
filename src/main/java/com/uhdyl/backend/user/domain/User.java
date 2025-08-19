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
import org.openapitools.jackson.nullable.JsonNullable;

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

    @Column(unique = true)
    private String nickname;

    private String picture;

    @Enumerated(value = EnumType.STRING)
    private OAuth2Provider provider;

    private String providerId;

    private String publicId;

    @Column(precision = 9, scale = 6)
    private BigDecimal locationX;

    @Column(precision = 9, scale = 6)
    private BigDecimal locationY;
  
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    /*
    권한을 계층적으로 설정했기에(FARMER > USER)
    구매자/판매자 중 어떤 상태인지 기존의 권한만으로는 표시할 수 없음
    판매자 -> 구매자 전환 시 권한을 FARMER -> USER로 변환하는 것은 부자연스러움
    따라서 현재의 권한(구매자/판매자)를 구별할 수 있는 필드 mode를 추가
     */
    private String mode;

    @Builder
    public User(String email, UserRole role, String name, String picture, OAuth2Provider provider, String providerId, String mode) {
        this.email = email;
        this.role = role;
        this.name = name;
        this.picture = picture;
        this.provider = provider;
        this.providerId = providerId;
        this.mode = mode;
    }

    public void deleteImage(){
        this.picture = null;
        this.publicId = null;
    }

    public void updateLocation(BigDecimal locationX, BigDecimal locationY){
        this.locationX = locationX;
        this.locationY = locationY;
    }
    public void addReview(Review review){
        this.reviews.add(review);
        review.setUser(this);
    }

    public void deleteReview(Review review){
        this.reviews.remove(review);
        review.setUser(null);
    }

    public void addProduct(Product product) {
        if (product == null) return;
        this.products.add(product);
        product.setUser(this);
    }

    public void removeProduct(Product product) {
        if (product == null) return;
        this.products.remove(product);
        product.setUser(null);
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateProfile(JsonNullable<String> picture, JsonNullable<String> nickname, JsonNullable<String> mode){

        if(picture.isPresent())
            this.picture = picture.get();
        if(nickname.isPresent())
            this.nickname = nickname.get();
        if(mode.isPresent())
            this.mode = mode.get();
    }

    public void updateUserToFarmer(){
        this.role = UserRole.FARMER;
    }

    public boolean isBBatRegistered(){
        if (this.locationX == null || this.locationY == null)
            return false;
        return true;
    }

    public void updateMode(String mode){
        this.mode = mode;
    }
}
