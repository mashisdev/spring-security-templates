package com.jwt.simple.user.controller;

import com.jwt.simple.exception.user.NotAllowedToChangeCredentialsException;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.request.UpdateUserRequest;
import com.jwt.simple.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PutMapping()
    public ResponseEntity<UserDto> update(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
        UserDto currentUserEmail = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        UserDto updatedUser = userService.findById(updateUserRequest.getId());

        if (!currentUserEmail.getId().equals(updatedUser.getId())) {
            throw new NotAllowedToChangeCredentialsException("Not allowed to change another user's credentials");
        }

        return ResponseEntity.ok(userService.update(userMapper.updateUserRequestToUserDto(updateUserRequest)));
    }

    @Override
    @DeleteMapping()
    public ResponseEntity<Void> delete(Long id) {
        return null;
    }
}
