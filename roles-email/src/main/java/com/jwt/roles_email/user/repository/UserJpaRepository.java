package com.jwt.roles_email.user.repository;

import com.jwt.roles_email.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity,UUID> {
    Boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}
