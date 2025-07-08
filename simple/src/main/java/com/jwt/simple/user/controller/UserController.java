package com.jwt.simple.user.controller;

import com.jwt.simple.user.dto.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

    ResponseEntity<UserDto> findMeByEmail();

    ResponseEntity<UserDto> findById(Long id);

    ResponseEntity<List<UserDto>> findAll();

    ResponseEntity<UserDto> update(UserDto userDto);

    ResponseEntity<Void> delete(Long id);
}
