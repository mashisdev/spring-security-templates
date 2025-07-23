package com.jwt.roles.user.controller;

import com.jwt.roles.user.dto.UserDto;
import com.jwt.roles.user.request.UpdateUserRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

    ResponseEntity<UserDto> findMeByEmail();

    ResponseEntity<UserDto> findById(Long id);

    ResponseEntity<List<UserDto>> findAll();

    ResponseEntity<UserDto> update(Long id, UpdateUserRequest updateUserRequest);

    ResponseEntity<Void> delete(Long id);
}
