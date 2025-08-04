package com.jwt.roles.auth;

import com.jwt.roles.auth.request.AuthenticationRequest;
import com.jwt.roles.auth.request.RegisterRequest;
import com.jwt.roles.auth.response.AuthenticationResponse;
import com.jwt.roles.exception.ErrorMessage;
import com.jwt.roles.user.entity.User;
import com.jwt.roles.user.mapper.UserMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "APIs for registering and logging users")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details and returns an authentication token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully and returns a token.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. The request body is invalid or has validation errors.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict. A user with the same email already exists.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PostMapping("/register")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());

        User user = userMapper.registerRequestToUser(request);
        log.debug("Mapped RegisterRequest to User object for email: {}", user.getEmail());

        AuthenticationResponse response = authenticationService.register(user);
        log.info("User registered successfully. Returning authentication token.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user with email and password and returns an authentication token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully and returns a token.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. The request body is invalid or has validation errors.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Invalid email or password.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @PostMapping("/login")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        log.info("Received login request for email: {}", request.getEmail());

        AuthenticationResponse response = authenticationService.authenticate(request);
        log.info("User logged in successfully. Returning authentication token.");
        return ResponseEntity.ok(response);
    }

}
