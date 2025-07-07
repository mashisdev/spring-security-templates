package com.jwt.simple.user.repository;

import com.jwt.simple.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}
