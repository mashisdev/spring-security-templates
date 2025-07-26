package com.jwt.roles_email.user.repository;

import com.jwt.roles_email.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User userEntity);

    Optional<User> findById(UUID id);

    List<User> findAll();

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    void deleteById(UUID id);
}
