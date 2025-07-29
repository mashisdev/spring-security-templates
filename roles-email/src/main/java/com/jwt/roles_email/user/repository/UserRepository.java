package com.jwt.roles_email.user.repository;

import com.jwt.roles_email.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User userEntity);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    List<User> findAll();

    Boolean existsByEmail(String email);

    void deleteById(Long id);
}
