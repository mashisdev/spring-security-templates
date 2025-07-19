package com.jwt.simple.user.service;

import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.entity.User;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findById(Long id) {
        log.info("Attempting to find user DTO by ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            log.warn("User with ID: {} not found for findById operation.", id);
            throw new UserNotFoundException("User not found");
        }
        log.debug("User found with ID: {}", id);
        UserDto userDto = userMapper.userToUserDto(userOptional.get());
        log.debug("Mapped User to UserDto for ID: {}", id);
        return userDto;
    }

    @Override
    public UserDto findByEmail(String email) {
        log.info("Attempting to find user DTO by email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.warn("User with email: {} not found for findByEmail operation.", email);
            throw new UserNotFoundException("User not found");
        }
        log.debug("User found with email: {}", email);
        UserDto userDto = userMapper.userToUserDto(userOptional.get());
        log.debug("Mapped User to UserDto for email: {}", email);
        return userDto;
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Attempting to find all user DTOs.");
        List<UserDto> userDtos = userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .toList();
        log.info("Found {} user DTOs.", userDtos.size());
        return userDtos;
    }

    @Override
    public UserDto update(UserDto userDto) {
        log.info("Attempting to update user with ID: {}", userDto.getId());
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> {
                    log.warn("User with ID: {} not found for update operation.", userDto.getId());
                    return new UserNotFoundException("User not found");
                });
        log.debug("Existing user found for ID: {}", userDto.getId());

        BeanUtils.copyProperties(userMapper.userDtoToUser(userDto), existingUser, "password");
        log.debug("Properties copied for user ID: {}. Excluding password.", userDto.getId());

        User updatedUser = userRepository.save(existingUser);
        log.info("User with ID: {} updated successfully.", updatedUser.getId());
        return userMapper.userToUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        log.info("Attempting to delete user with ID: {}", id);
        try {
            userRepository.deleteById(id);
            log.info("User with ID: {} deleted successfully.", id);
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}. Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
