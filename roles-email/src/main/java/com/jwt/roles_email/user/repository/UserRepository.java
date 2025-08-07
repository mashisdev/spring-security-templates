package com.jwt.roles_email.user.repository;

import com.jwt.roles_email.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    User save(User userEntity);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    Page<User> findAll(Pageable pageable);

    Boolean existsByEmail(String email);

    void deleteById(Long id);
}
