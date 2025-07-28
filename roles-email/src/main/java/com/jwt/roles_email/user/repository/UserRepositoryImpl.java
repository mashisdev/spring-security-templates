package com.jwt.roles_email.user.repository;

import com.jwt.roles_email.user.mapper.UserMapper;
import com.jwt.roles_email.user.entity.User;
import com.jwt.roles_email.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User userEntity) {
        UserEntity entity = userMapper.userToUserEntity(userEntity);
        UserEntity saved = userJpaRepository.save(entity);
        return userMapper.userEntityToUser(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::userEntityToUser);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream().map(userMapper::userEntityToUser).toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::userEntityToUser);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

}
