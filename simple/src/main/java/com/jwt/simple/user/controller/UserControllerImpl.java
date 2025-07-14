package com.jwt.simple.user.controller;

import com.jwt.simple.exception.user.NotAllowedToChangeCredentialsException;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.request.UpdateUserRequest;
import com.jwt.simple.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @GetMapping("/me")
    public ResponseEntity<UserDto> findMeByEmail() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.findByEmail(user));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest updateUserRequest) {
        UserDto currentUser = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        UserDto updatedUser = userService.findById(updateUserRequest.getId());

        if (!currentUser.getId().equals(updatedUser.getId()) || !currentUser.getId().equals(id)) {
            throw new NotAllowedToChangeCredentialsException("Not allowed to change another user's credentials");
        }

        return ResponseEntity.ok(userService.update(userMapper.updateUserRequestToUserDto(updateUserRequest)));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserDto currentUser = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!currentUser.equals(userService.findById(id))) {
            throw new NotAllowedToChangeCredentialsException("Not allowed to change another user's credentials");
        }

        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
