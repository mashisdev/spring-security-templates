package com.jwt.roles_email.user.controller;

import com.jwt.roles_email.user.dto.UserDto;
import com.jwt.roles_email.user.request.UpdateUserRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

    ResponseEntity<UserDto> findMeByEmail();

    ResponseEntity<UserDto> findById(Long id);

    ResponseEntity<List<UserDto>> findAll();

    ResponseEntity<UserDto> update(Long id, UpdateUserRequest updateUserRequest);

    ResponseEntity<Void> delete(Long id);
}
