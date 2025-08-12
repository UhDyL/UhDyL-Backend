package com.uhdyl.backend.zzim.domain;

import com.uhdyl.backend.global.base.BaseEntity;
import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_zzim_user_product",
                columnNames = {"user_id", "product_id"}
        ),
        indexes = {
                @Index(name = "idx_zzim_user", columnList = "user_id"),
                @Index(name = "idx_zzim_product", columnList = "product_id")
        }
)
public class Zzim extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Zzim(Product product, User user) {
        this.product = product;
        this.user = user;
    }
}
