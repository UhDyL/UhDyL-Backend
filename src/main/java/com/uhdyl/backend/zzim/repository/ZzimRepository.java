package com.uhdyl.backend.zzim.repository;

import com.uhdyl.backend.product.domain.Product;
import com.uhdyl.backend.user.domain.User;
import com.uhdyl.backend.zzim.domain.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZzimRepository extends JpaRepository<Zzim, Long>, CustomZzimRepository {
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);

    Zzim findByUserAndProduct(User user, Product product);

    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
}
