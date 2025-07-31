package com.jwt.simple.user.controller;

import com.jwt.simple.exception.ErrorMessage;
import com.jwt.simple.exception.user.NotAllowedToChangeCredentialsException;
import com.jwt.simple.user.dto.UserDto;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.request.UpdateUserRequest;
import com.jwt.simple.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for reading, updating and deleting users")
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get authenticated user details", description = "Retrieves the details of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved authenticated user details.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or failed. " +
                            "**Message:** You are not authenticated. Please log in to access this resource.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Find user by ID", description = "Retrieves the details of a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user details.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping("/{id}")
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        log.info("Received request to find user by ID: {}", id);

        UserDto userDto = userService.findById(id);
        log.info("Successfully retrieved user with ID: {}", id);
        return ResponseEntity.ok(userDto);
    }

    @Override
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Find all users", description = "Retrieves a list of all users. This may be restricted to administrators in a real application.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of all users.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto[].class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping()
    @RateLimiter(name = "userRateLimiter")
    public ResponseEntity<List<UserDto>> findAll() {
        log.info("Received request to find all users");

        List<UserDto> users = userService.findAll();
        log.info("Successfully retrieved all users. Total: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Override
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user details", description = "Updates the details of a user. An authenticated user can only update their own details.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation failed. " +
                            "**Message:** Validation failed.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or failed. " +
                            "**Message:** You are not authenticated. Please log in to access this resource.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to change another user's credentials. " +
                            "**Message:** Not allowed to change another user's credentials.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a user", description = "Deletes a user by ID. An authenticated user can only delete their own account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User successfully deleted. No content to return."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or failed. " +
                            "**Message:** You are not authenticated. Please log in to access this resource.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed to change another user's credentials. " +
                            "**Message:** Not allowed to change another user's credentials.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. " +
                            "**Message:** User not found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests. Rate limit exceeded. " +
                            "**Message:** Too many requests. Please try again later.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
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
