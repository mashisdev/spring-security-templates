package com.jwt.simple.user.service;

import com.jwt.simple.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto update(UserDto userDto);

    List<UserDto> findAll();

    UserDto findById(Long id);

    void delete(Long id);

}
