package com.jwt.roles_email.auth;

import com.jwt.roles_email.config.service.EmailService;
import com.jwt.roles_email.config.service.JwtService;
import com.jwt.roles_email.exception.user.UserAlreadyRegisteredException;
import com.jwt.roles_email.user.dto.UserDto;
import com.jwt.roles_email.user.entity.Role;
import com.jwt.roles_email.user.entity.User;
import com.jwt.roles_email.user.mapper.UserMapper;
import com.jwt.roles_email.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

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

        log.debug("Attempting to send verification email to user: {}", user.getEmail());
        sendVerificationEmail(user);
        log.debug("Verification email sent successfully to user: {}", user.getEmail());

        userRepository.save(user);
        log.info("User {} registered successfully with ID: {}", user.getEmail(), user.getId());

        return userMapper.userToUserDto(user);
    }

//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        log.info("Attempting to authenticate user with email: {}", request.email());
//
//        Optional<User> userOptional = userRepository.findByEmail(request.email());
//        if (userOptional.isEmpty()) {
//            log.warn("Authentication failed: User with email {} not found.", request.email());
//            throw new WrongEmailOrPasswordException("Wrong email or password");
//        }
//        User user = userOptional.get();
//        log.debug("User found for email: {}", user.getEmail());
//
//        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
//            log.warn("Authentication failed: Password mismatch for user {}.", request.email());
//            throw new WrongEmailOrPasswordException("Wrong email or password");
//        }
//        log.debug("Password matched for user: {}", user.getEmail());
//
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.email(),
//                        request.password()
//                )
//        );
//        log.info("User {} authenticated successfully via AuthenticationManager.", request.email());
//
//        String jwtToken = jwtService.generateToken(userMapper.userToUserEntity(user));
//        log.debug("JWT token generated for authenticated user: {}", user.getEmail());
//
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
//
//    }

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
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Integer generateVerificationCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }
}

