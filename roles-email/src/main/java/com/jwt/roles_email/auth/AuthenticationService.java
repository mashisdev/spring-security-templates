package com.jwt.roles_email.auth;

import com.jwt.roles_email.auth.request.AuthenticationRequest;
import com.jwt.roles_email.auth.request.VerifyRequest;
import com.jwt.roles_email.auth.response.AuthenticationResponse;
import com.jwt.roles_email.config.service.EmailService;
import com.jwt.roles_email.config.service.JwtService;
import com.jwt.roles_email.exception.user.UserAlreadyRegisteredException;
import com.jwt.roles_email.exception.user.UserNotFoundException;
import com.jwt.roles_email.exception.user.WrongEmailOrPasswordException;
import com.jwt.roles_email.exception.validation.*;
import com.jwt.roles_email.user.dto.UserDto;
import com.jwt.roles_email.user.entity.Role;
import com.jwt.roles_email.user.entity.User;
import com.jwt.roles_email.user.entity.UserEntity;
import com.jwt.roles_email.user.mapper.UserMapper;
import com.jwt.roles_email.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Value("${RESET_TOKEN_EXPIRATION}")
    private long RESET_TOKEN_EXPIRATION;

    public UserDto register(User user) {
        log.info("Attempting to register new user with email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Registration failed: User with email {} is already registered.", user.getEmail());
            throw new UserAlreadyRegisteredException("User already registered");
        }
        log.debug("User with email {} not found, proceeding with registration.", user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.debug("Password encoded for user: {}", user.getEmail());

        user.setRole(Role.USER);
        log.debug("Role assigned to user: {}", user.getEmail());

        user.setEnabled(false);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(30));
        log.debug("Verification code generated and set for user {}. Code expires at: {}", user.getEmail(), user.getVerificationCodeExpiresAt());

        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        log.debug("Reset token and its expiration set to null for new user: {}", user.getEmail());

        log.debug("Attempting to send verification email to user: {}", user.getEmail());
        sendVerificationEmail(user);
        log.debug("Verification email sent successfully to user: {}", user.getEmail());

        User saved = userRepository.save(user);
        log.info("User {} registered successfully with ID: {}", saved.getEmail(), saved.getId());

        return userMapper.userToUserDto(saved);
    }

    public void verify(VerifyRequest verifyRequest) {
        log.info("Attempting to verify user with email: {}", verifyRequest.email());

        Optional<User> optionalUser = userRepository.findByEmail(verifyRequest.email());
        if (optionalUser.isEmpty()) {
            log.warn("Verification failed: User with email {} not found.", verifyRequest.email());
            throw new UserNotFoundException("User not found");
        }

        User user = optionalUser.get();
        log.debug("User found for email: {}. Proceeding with verification checks.", user.getEmail());

        if (user.isEnabled()) {
            log.warn("Verification failed: Account for user {} is already verified.", user.getEmail());
            throw new AccountAlreadyVerifiedException("Account is already verified");
        }

        if (!user.getVerificationCode().equals(verifyRequest.verificationCode())) {
            log.warn("Verification failed for user {}: Invalid verification code provided.", user.getEmail());
            throw new InvalidVerificationCodeException("Invalid verification code");
        }
        log.debug("Verification code matched for user: {}", user.getEmail());

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Verification failed for user {}: Verification code has expired.", user.getEmail());
            throw new VerificationCodeExpiredException("Verification code has expired");
        }
        log.debug("Verification code for user {} is still valid.", user.getEmail());

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);

        log.info("User {} account verified successfully.", user.getEmail());
    }

    public void resendVerificationCode(VerifyRequest verifyRequest) {
        log.info("Attempting to resend verification code for user with email: {}", verifyRequest.email());

        Optional<User> optionalUser = userRepository.findByEmail(verifyRequest.email());

        if (optionalUser.isEmpty()) {
            log.warn("Resend verification code failed: User with email {} not found.", verifyRequest.email());
            throw new UserNotFoundException("User not found");
        }

        User user = optionalUser.get();
        log.debug("User found for email: {}. Proceeding with resend checks.", user.getEmail());

        if (user.isEnabled()) {
            log.warn("Resend verification code failed: Account for user {} is already verified. No need to resend code.", user.getEmail());
            throw new AccountAlreadyVerifiedException("Account is already verified");
        }

        if (user.getVerificationCodeExpiresAt() != null && user.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now())) {
            log.warn("Resend verification code failed for user {}: Current verification code is still valid and not expired yet (expires at {}).",
                    user.getEmail(), user.getVerificationCodeExpiresAt());
            throw new VerificationCodeStillValidException("Current verification code is still valid");
        }
        log.debug("Current verification code for user {} is expired or not set. Generating a new one.", user.getEmail());

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(30));
        log.debug("New verification code generated and set for user {}. It will expire at: {}", user.getEmail(), user.getVerificationCodeExpiresAt());

        log.debug("Attempting to send new verification email to user: {}", user.getEmail());
        sendVerificationEmail(user);
        log.debug("New verification email sent successfully to user: {}", user.getEmail());

        userRepository.save(user);
        log.info("Verification code successfully re-sent for user: {}", user.getEmail());
    }

    public void redeemPassword(String email) {
        log.info("Attempting to generate password reset token for user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password reset failed: User with email {} not found for token generation.", email);
                    return new UserNotFoundException("User not found");
                });
        log.debug("User found for password reset token generation: {}", email);

        if (!user.isEnabled()) {
            log.warn("Password reset failed for user {}: Account is not enabled.", user.getEmail());
            throw new AccountNotVerifiedException("Account not verified. Please verify your account.");
        }

        var token = UUID.randomUUID().toString();
        var expiration = Instant.now().plusSeconds(RESET_TOKEN_EXPIRATION);

        user.setResetToken(token);
        user.setResetTokenExpiration(expiration);
        log.debug("New reset token generated for user {} expiring at {}.", user.getEmail(), expiration);

        sendPasswordResetEmail(user.getEmail(), token);
        userRepository.save(user);
        log.info("Reset token successfully saved for user {}.", user.getEmail());

    }

    public void resetPassword(String token, String password) {
        log.info("Attempting to reset password for a user using a reset token.");
        log.debug("Received token for password reset: {}", token);

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> {
                    log.warn("Password reset failed: User not found for provided reset token.");
                    return new UserNotFoundException("Invalid or non-existent reset token provided.");
                });
        log.debug("User found with email {} for password reset. Checking token expiration.", user.getEmail());

        if (user.getResetTokenExpiration() == null || user.getResetTokenExpiration().isBefore(Instant.now())) {
            log.warn("Password reset failed for user {}: Reset token has expired or is null.", user.getEmail());
            throw new PasswordResetTokenExpiredException("Password reset token has expired.");
        }
        log.debug("Reset token for user {} is valid and not expired. Proceeding to update password.", user.getEmail());

        user.setPassword(passwordEncoder.encode(password));
        log.debug("New password encoded and set for user {}.", user.getEmail());

        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        log.debug("Reset token and its expiration cleared for user {}.", user.getEmail());

        userRepository.save(user);
        log.info("Password successfully reset and saved for user {}.", user.getEmail());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Attempting to authenticate user with email: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Authentication failed for email {}: User not found.", request.email());
                    return new WrongEmailOrPasswordException("Wrong email or password");
                });
        log.debug("User found for email: {}. Proceeding with password verification.", user.getEmail());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Authentication failed for user {}: Incorrect password provided.", user.getEmail());
            throw new WrongEmailOrPasswordException("Wrong email or password");
        }
        log.debug("Password matched for user: {}.", user.getEmail());

        if (!user.isEnabled()) {
            log.warn("Authentication failed for user {}: Account not verified.", user.getEmail());
            throw new AccountNotVerifiedException("Account not verified. Please verify your account.");
        }
        log.debug("Account for user {} is enabled. Proceeding with Spring Security authentication.", user.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        log.info("User {} authenticated successfully via AuthenticationManager.", request.email());

        UserEntity userEntity = userMapper.userToUserEntity(user);
        log.debug("Mapped internal User model to Spring Security UserDetails for token generation for user: {}", user.getEmail());

        String jwtToken = jwtService.generateToken(userEntity);
        log.info("JWT token generated successfully for user: {}. Authentication process completed.", user.getEmail());

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private void sendVerificationEmail(User user) {
        String subject = "Jwt Email Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            log.debug("Attempting to send verification email to {}. Subject: {}", user.getEmail(), subject);
            emailService.sendEmail(user.getEmail(), subject, htmlMessage);
            log.info("Verification email sent successfully to user: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to user: {}. Error: {}", user.getEmail(), e.getMessage(), e);
        }
    }

    private Integer generateVerificationCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    private void sendPasswordResetEmail(String email, String token) {
        String subject = "Password Reset Request";
        String resetUrl = "http://localhost:4200/reset-password?token=" + token; // Example of resetUrl for Angular frontend
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Password Reset Request</h2>"
                + "<p style=\"font-size: 16px;\">We received a request to reset your password. Please click the button below to set a new password:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); text-align: center;\">"
                + "<p>"
                + "<a href=\"" + resetUrl + "\" style=\""
                + "display: inline-block; "
                + "padding: 10px 20px; "
                + "font-size: 18px; "
                + "font-weight: bold; "
                + "color: #ffffff; "
                + "background-color: #007bff; "
                + "text-decoration: none; "
                + "border-radius: 5px;"
                + "\">Reset My Password</a>"
                + "</p>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #666;\">If you did not request a password reset, please ignore this email.</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            log.debug("Attempting to send password reset email to {}. Subject: {}", email, subject);
            emailService.sendEmail(email, subject, htmlMessage);
            log.info("Password reset email sent successfully to user: {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to user: {}. Error: {}", email, e.getMessage(), e);
        }
    }
}

