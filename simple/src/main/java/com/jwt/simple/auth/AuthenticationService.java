package com.jwt.simple.auth;

import com.jwt.simple.auth.request.AuthenticationRequest;
import com.jwt.simple.auth.request.RegisterRequest;
import com.jwt.simple.auth.response.AuthenticationResponse;
import com.jwt.simple.config.JwtService;
import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.exception.user.UserAlreadyExistException;
import com.jwt.simple.exception.user.WrongEmailOrPasswordException;
import com.jwt.simple.user.entity.User;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthenticationResponse register(RegisterRequest request) {

        User user = userMapper.registerRequestToUser(request);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) throw new UserAlreadyExistException("User already registered");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user);

        String jwtToken = jwtService.generateToken(userMapper.userToUserEntity(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new WrongEmailOrPasswordException("Wrong email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongEmailOrPasswordException("Wrong email or password");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String jwtToken = jwtService.generateToken(userMapper.userToUserEntity(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}

