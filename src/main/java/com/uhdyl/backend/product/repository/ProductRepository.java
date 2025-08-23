package com.uhdyl.backend.product.repository;


import com.uhdyl.backend.product.domain.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {
    Optional<Product> findByIdAndUser_Id(Long productId, Long userId);

    Optional<Product> findByUser_Id(Long userId);
}
