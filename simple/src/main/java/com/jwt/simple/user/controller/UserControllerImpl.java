package com.jwt.simple.user.controller;

import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.service.UserService;
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
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return null;
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        return null;
    }
}
