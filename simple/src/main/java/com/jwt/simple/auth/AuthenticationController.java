package com.jwt.simple.auth;

import com.jwt.simple.auth.request.AuthenticationRequest;
import com.jwt.simple.auth.request.RegisterRequest;
import com.jwt.simple.auth.response.AuthenticationResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/register")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());

        AuthenticationResponse response = authenticationService.register(request);
        log.info("User registered successfully. Returning authentication token.");
        return ResponseEntity.ok(response);
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
