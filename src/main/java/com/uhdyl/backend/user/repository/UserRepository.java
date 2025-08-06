package com.uhdyl.backend.user.repository;

import com.uhdyl.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByIdAndPublicId(Long id, String publicId);

    boolean existsByNickname(String nickname);
}
