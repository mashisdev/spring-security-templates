package com.jwt.roles.auth;

import com.jwt.roles.auth.request.AuthenticationRequest;
import com.jwt.roles.auth.request.RegisterRequest;
import com.jwt.roles.auth.response.AuthenticationResponse;
import com.jwt.roles.user.entity.User;
import com.jwt.roles.user.mapper.UserMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

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

    @PostMapping("/login")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        log.info("Received login request for email: {}", request.getEmail());

        AuthenticationResponse response = authenticationService.authenticate(request);
        log.info("User logged in successfully. Returning authentication token.");
        return ResponseEntity.ok(response);
    }

}
