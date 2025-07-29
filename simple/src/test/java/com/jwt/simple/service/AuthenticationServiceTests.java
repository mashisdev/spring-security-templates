package com.jwt.simple.service;

import com.jwt.simple.auth.AuthenticationService;
import com.jwt.simple.auth.request.AuthenticationRequest;
import com.jwt.simple.auth.request.RegisterRequest;
import com.jwt.simple.auth.response.AuthenticationResponse;
import com.jwt.simple.config.JwtService;
import com.jwt.simple.exception.user.UserAlreadyRegisteredException;
import com.jwt.simple.exception.user.WrongEmailOrPasswordException;
import com.jwt.simple.user.entity.User;
import com.jwt.simple.user.entity.UserEntity;
import com.jwt.simple.user.mapper.UserMapper;
import com.jwt.simple.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private User user;
    private User userWithEncodedPassword;
    private UserEntity userEntity;
    private String encodedPassword;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        authenticationRequest = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        encodedPassword = "encodedPassword123";
        jwtToken = "dummyJwtToken";

        userWithEncodedPassword = User.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password(encodedPassword)
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john.doe@example.com")
                .password(encodedPassword)
                .build();
    }

    // Register tests

    @Test
    void register_ShouldReturnAuthenticationResponse_WhenUserIsNotRegistered() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userMapper.registerRequestToUser(any(RegisterRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(userWithEncodedPassword);
        when(userMapper.userToUserEntity(any(User.class))).thenReturn(userEntity);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn(jwtToken);

        AuthenticationResponse response = authenticationService.register(userMapper.registerRequestToUser(registerRequest));

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());

        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(userMapper, times(1)).registerRequestToUser(registerRequest);
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).userToUserEntity(any(User.class));
        verify(jwtService, times(1)).generateToken(userEntity);
    }

    @Test
    void register_ShouldThrowUserAlreadyRegisteredException_WhenUserIsAlreadyRegistered() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.registerRequestToUser(any(RegisterRequest.class))).thenReturn(user);

        UserAlreadyRegisteredException thrown = assertThrows(UserAlreadyRegisteredException.class, () -> {
            authenticationService.register(userMapper.registerRequestToUser(registerRequest));
        });

        assertEquals("User already registered", thrown.getMessage());

        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(userMapper, times(1)).registerRequestToUser(any(RegisterRequest.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(UserEntity.class));
    }


    // Authenticate tests

    @Test
    void authenticate_ShouldReturnAuthenticationResponse_WhenCredentialsAreCorrect() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(userWithEncodedPassword));
        when(passwordEncoder.matches(authenticationRequest.getPassword(), userWithEncodedPassword.getPassword())).thenReturn(true);

        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        when(userMapper.userToUserEntity(any(User.class))).thenReturn(userEntity);
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn(jwtToken);

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());

        verify(userRepository, times(1)).findByEmail(authenticationRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(authenticationRequest.getPassword(), userWithEncodedPassword.getPassword());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userMapper, times(1)).userToUserEntity(userWithEncodedPassword);
        verify(jwtService, times(1)).generateToken(userEntity);
    }


    @Test
    void authenticate_ShouldThrowWrongEmailOrPasswordException_WhenEmailNotFound() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.empty());

        WrongEmailOrPasswordException thrown = assertThrows(WrongEmailOrPasswordException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });

        assertEquals("Wrong email or password", thrown.getMessage());

        verify(userRepository, times(1)).findByEmail(authenticationRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(UserEntity.class));
    }

    @Test
    void authenticate_ShouldThrowWrongEmailOrPasswordException_WhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())).thenReturn(false);

        WrongEmailOrPasswordException thrown = assertThrows(WrongEmailOrPasswordException.class, () -> {
            authenticationService.authenticate(authenticationRequest);
        });

        assertEquals("Wrong email or password", thrown.getMessage());

        verify(userRepository, times(1)).findByEmail(authenticationRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(authenticationRequest.getPassword(), user.getPassword());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(UserEntity.class));
    }
}