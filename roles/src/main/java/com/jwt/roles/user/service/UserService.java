package com.jwt.roles.user.service;

import com.jwt.roles.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto findById(Long id);

    UserDto findByEmail(String email);

    List<UserDto> findAll();

    UserDto update(UserDto userDto);

    void delete(Long id);

}
