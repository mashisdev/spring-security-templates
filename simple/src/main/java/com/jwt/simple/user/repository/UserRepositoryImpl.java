package com.jwt.simple.user.repository;

import com.jwt.simple.user.entity.User;
import com.jwt.simple.user.entity.UserEntity;
import com.jwt.simple.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        log.info("Saving user with email: {}", user.getEmail());
        UserEntity entity = userMapper.userToUserEntity(user);
        log.debug("Mapped User to UserEntity for saving. User ID: {}", entity.getId());
        UserEntity saved = userJpaRepository.save(entity);
        log.info("User saved successfully with ID: {}", saved.getId());
        return userMapper.userEntityToUser(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        log.info("Attempting to find user by ID: {}", id);
        Optional<UserEntity> userEntity = userJpaRepository.findById(id);
        if (userEntity.isPresent()) {
            log.debug("User found with ID: {}", id);
            return userEntity.map(userMapper::userEntityToUser);
        } else {
            log.warn("User with ID: {} not found.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        log.info("Fetching all users from the database.");
        List<User> users = userJpaRepository.findAll().stream()
                .map(userMapper::userEntityToUser)
                .toList();
        log.debug("Found {} users in total.", users.size());
        return users;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Attempting to find user by email: {}", email);
        Optional<UserEntity> userEntity = userJpaRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            log.debug("User found with email: {}", email);
            return userEntity.map(userMapper::userEntityToUser);
        } else {
            log.warn("User with email: {} not found.", email);
            return Optional.empty();
        }
    }

    @Override
    public Boolean existsByEmail(String email) {
        log.info("Checking if user with email: {} exists.", email);
        Boolean exists = userJpaRepository.existsByEmail(email);
        log.debug("User with email: {} exists: {}", email, exists);
        return exists;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Attempting to delete user by ID: {}", id);
        try {
            userJpaRepository.deleteById(id);
            log.info("User with ID: {} deleted successfully.", id);
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}. Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

}

