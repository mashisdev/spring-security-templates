package com.jwt.simple.user.service;

import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.entity.User;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findById(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    public UserDto findByEmail(String email) {
        return userMapper.userToUserDto(userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::userToUserDto).toList();
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        BeanUtils.copyProperties(userMapper.userDtoToUser(userDto), user, "password");
        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
