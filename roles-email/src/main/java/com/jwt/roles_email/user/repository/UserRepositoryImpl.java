package com.jwt.roles_email.user.repository;

import com.jwt.roles_email.user.mapper.UserMapper;
import com.jwt.roles_email.user.entity.User;
import com.jwt.roles_email.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SpringJpaRepository springJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User userEntity) {
        UserEntity entity = userMapper.userToUserEntity(userEntity);
        UserEntity saved = springJpaRepository.save(entity);
        return userMapper.userEntityToUser(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springJpaRepository.findById(id).map(userMapper::userEntityToUser);
    }

    @Override
    public List<User> findAll() {
        return springJpaRepository.findAll().stream().map(userMapper::userEntityToUser).toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springJpaRepository.findByEmail(email).map(userMapper::userEntityToUser);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return springJpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        springJpaRepository.deleteById(id);
    }

}
