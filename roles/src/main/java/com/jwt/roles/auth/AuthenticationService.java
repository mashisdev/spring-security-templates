package com.jwt.roles.auth;

import com.jwt.roles.auth.request.AuthenticationRequest;
import com.jwt.roles.auth.response.AuthenticationResponse;
import com.jwt.roles.config.JwtService;
import com.jwt.roles.exception.user.UserAlreadyRegisteredException;
import com.jwt.roles.exception.user.WrongEmailOrPasswordException;
import com.jwt.roles.user.entity.Role;
import com.jwt.roles.user.entity.User;
import com.jwt.roles.user.mapper.UserMapper;
import com.jwt.roles.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthenticationResponse register(User user) {
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

        User saved = userRepository.save(user);
        log.info("User {} registered successfully with ID: {}", saved.getEmail(), saved.getId());

        String jwtToken = jwtService.generateToken(userMapper.userToUserEntity(user));
        log.debug("JWT token generated for registered user: {}", saved.getEmail());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Attempting to authenticate user with email: {}", request.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            log.warn("Authentication failed: User with email {} not found.", request.getEmail());
            throw new WrongEmailOrPasswordException("Wrong email or password");
        }
        User user = userOptional.get();
        log.debug("User found for email: {}", user.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: Password mismatch for user {}.", request.getEmail());
            throw new WrongEmailOrPasswordException("Wrong email or password");
        }
        log.debug("Password matched for user: {}", user.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        log.info("User {} authenticated successfully via AuthenticationManager.", request.getEmail());

        String jwtToken = jwtService.generateToken(userMapper.userToUserEntity(user));
        log.debug("JWT token generated for authenticated user: {}", user.getEmail());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}

