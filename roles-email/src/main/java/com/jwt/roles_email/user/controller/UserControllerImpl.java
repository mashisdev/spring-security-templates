package com.jwt.roles_email.user.controller;

import com.jwt.roles_email.exception.user.NotAllowedToChangeCredentialsException;
import com.jwt.roles_email.user.dto.UserDto;
import com.jwt.roles_email.user.entity.Role;
import com.jwt.roles_email.user.entity.UserEntity;
import com.jwt.roles_email.user.mapper.UserMapper;
import com.jwt.roles_email.user.request.UpdateUserRequest;
import com.jwt.roles_email.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<UserDto> findMeByEmail(@AuthenticationPrincipal UserEntity authenticatedUser) {
        log.info("Received request to get authenticated user's details for email: {}", authenticatedUser.getEmail());
        UserDto userDto = userService.findByEmail(authenticatedUser.getEmail());
        log.info("Successfully retrieved details for user: {}", authenticatedUser.getEmail());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{id}")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        log.info("Received request to find user by ID: {}", id);

        UserDto userDto = userService.findById(id);
        log.info("Successfully retrieved user with ID: {}", id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<Page<UserDto>> findAll(@PageableDefault(size = 10) Pageable pageable) {
        log.info("Received request to find all users");

        Page<UserDto> users = userService.findAll(pageable);
        log.info("Successfully retrieved all users. Total: {}", users.getSize());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                          @RequestBody @Valid UpdateUserRequest updateUserRequest,
                                          @AuthenticationPrincipal UserEntity currentUser) {
        log.info("Received update request for user ID: {} from authenticated user: {}", id, currentUser.getEmail());

        UserDto targetUserInRequest = userService.findById(updateUserRequest.id());

        if (!currentUser.getId().equals(targetUserInRequest.getId()) || !currentUser.getId().equals(id)) {
            log.warn("Authorization failed: User {} attempted to update user ID {} (target ID in request: {}). Not allowed.",
                    currentUser, id, updateUserRequest.id());
            throw new NotAllowedToChangeCredentialsException("Not allowed to change another user's credentials");
        }
        log.debug("Authorization successful: User {} is allowed to update user ID {}.", currentUser, id);

        UserDto userToUpdate = userMapper.updateUserRequestToUserDto(updateUserRequest);
        userToUpdate.setRole(Role.USER);

        UserDto updatedUser = userService.update(userToUpdate);
        log.info("Successfully updated user with ID: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserEntity currentUser) {
        log.info("Received delete request for user ID: {} from authenticated user: {}", id, currentUser.getEmail());

        UserDto targetUserToDelete = userService.findById(id);

        if (!currentUser.getId().equals(targetUserToDelete.getId())) {
            log.warn("Authorization failed: User {} attempted to delete user ID {}. Not allowed.",
                    currentUser.getEmail(), id);
            throw new NotAllowedToChangeCredentialsException("Not allowed to change another user's credentials");
        }
        log.debug("Authorization successful: User {} is allowed to delete user ID {}.", currentUser.getEmail(), id);

        userService.delete(id);
        log.info("Successfully deleted user with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
