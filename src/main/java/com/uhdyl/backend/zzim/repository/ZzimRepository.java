package com.uhdyl.backend.zzim.repository;

import com.uhdyl.backend.zzim.domain.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZzimRepository extends JpaRepository<Zzim, Long>, CustomZzimRepository {
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
}
