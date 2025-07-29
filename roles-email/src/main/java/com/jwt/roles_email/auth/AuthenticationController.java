package com.jwt.roles_email.auth;

import com.jwt.roles_email.auth.request.*;
import com.jwt.roles_email.auth.response.AuthenticationResponse;
import com.jwt.roles_email.user.dto.UserDto;
import com.jwt.roles_email.user.entity.User;
import com.jwt.roles_email.user.mapper.UserMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Received registration request for email: {}", request.email());

        User user = userMapper.registerRequestToUser(request);
        log.debug("Mapped RegisterRequest to User object for email: {}", user.getEmail());

        UserDto response = authenticationService.register(user);
        log.info("User registered successfully. Returning authentication token.");

        URI location = URI.create("/api/users/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/verify")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<Map<String, String>> verify(@RequestBody @Valid VerifyRequest verifyRequest) {
        authenticationService.verify(verifyRequest);
        Map<String, String> response = Collections.singletonMap("verification", "Account verified successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<Map<String, String> > resendVerificationCode(@RequestBody @Valid VerifyRequest verifyRequest) {
        authenticationService.resendVerificationCode(verifyRequest);
        Map<String, String> response = Collections.singletonMap("verification", "Verification code sent");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        log.info("Received login request for email: {}", request.email());

        AuthenticationResponse response = authenticationService.authenticate(request);
        log.info("User logged in successfully. Returning authentication token.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redeem-password")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<Map<String, String>> redeemPassword(@RequestBody @Valid RedeemPasswordRequest request) {
        authenticationService.redeemPassword(request.email());
        return ResponseEntity.ok().body(Map.of("message", "Send the redeem password link to your email"));
    }

    @PostMapping("/reset-password")
    @RateLimiter(name = "authRateLimiter")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request.token(), request.password());
        return ResponseEntity.ok().body(Map.of("message", "Credentials updated"));
    }

}
