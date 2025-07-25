package com.jwt.simple.user.controller;

import com.jwt.simple.exception.user.NotAllowedToChangeCredentialsException;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.request.UpdateUserRequest;
import com.jwt.simple.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @GetMapping("/me")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<UserDto> findMeByEmail() {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Received request to get authenticated user's details for email: {}", authenticatedUserEmail);

        UserDto userDto = userService.findByEmail(authenticatedUserEmail);
        log.info("Successfully retrieved details for user: {}", authenticatedUserEmail);
        return ResponseEntity.ok(userDto);
    }

    @Override
    @GetMapping("/{id}")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        log.info("Received request to find user by ID: {}", id);

        UserDto userDto = userService.findById(id);
        log.info("Successfully retrieved user with ID: {}", id);
        return ResponseEntity.ok(userDto);
    }

    @Override
    @GetMapping()
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<List<UserDto>> findAll() {
        log.info("Received request to find all users");

        List<UserDto> users = userService.findAll();
        log.info("Successfully retrieved all users. Total: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Override
    @PutMapping("/{id}")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest updateUserRequest) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Received update request for user ID: {} from authenticated user: {}", id, authenticatedUserEmail);
        UserDto currentUser = userService.findByEmail(authenticatedUserEmail);
        UserDto targetUserInRequest = userService.findById(updateUserRequest.getId());

        if (!currentUser.getId().equals(targetUserInRequest.getId()) || !currentUser.getId().equals(id)) {
            log.warn("Authorization failed: User {} attempted to update user ID {} (target ID in request: {}). Not allowed.",
                    authenticatedUserEmail, id, updateUserRequest.getId());
            throw new NotAllowedToChangeCredentialsException("Not allowed to change another user's credentials");
        }
        log.debug("Authorization successful: User {} is allowed to update user ID {}.", authenticatedUserEmail, id);

        UserDto updatedUser = userService.update(userMapper.updateUserRequestToUserDto(updateUserRequest));
        log.info("Successfully updated user with ID: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    @Override
    @DeleteMapping("/{id}")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Received delete request for user ID: {} from authenticated user: {}", id, authenticatedUserEmail);

        UserDto currentUser = userService.findByEmail(authenticatedUserEmail);
        UserDto targetUserToDelete = userService.findById(id);

        if (!currentUser.getId().equals(targetUserToDelete.getId())) {
            log.warn("Authorization failed: User {} attempted to delete user ID {}. Not allowed.",
                    authenticatedUserEmail, id);
            throw new NotAllowedToChangeCredentialsException("Not allowed to change another user's credentials");
        }
        log.debug("Authorization successful: User {} is allowed to delete user ID {}.", authenticatedUserEmail, id);

        userService.delete(id);
        log.info("Successfully deleted user with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
