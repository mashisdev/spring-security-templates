package com.jwt.simple.user.controller;

import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.request.UpdateUserRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

    ResponseEntity<UserDto> findMeByEmail();

    ResponseEntity<UserDto> findById(Long id);

    ResponseEntity<List<UserDto>> findAll();

    ResponseEntity<UserDto> update(UpdateUserRequest updateUserRequest);

    ResponseEntity<Void> delete(Long id);
}
