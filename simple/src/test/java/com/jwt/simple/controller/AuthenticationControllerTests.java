package com.jwt.simple.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.simple.auth.AuthenticationController;
import com.jwt.simple.auth.AuthenticationService;
import com.jwt.simple.auth.request.AuthenticationRequest;
import com.jwt.simple.auth.request.RegisterRequest;
import com.jwt.simple.auth.response.AuthenticationResponse;
import com.jwt.simple.config.JwtAuthFilter;
import com.jwt.simple.exception.user.UserAlreadyRegisteredException;
import com.jwt.simple.exception.user.WrongEmailOrPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(    controllers = AuthenticationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        ),excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AuthenticationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;

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

        authenticationResponse = AuthenticationResponse.builder()
                .token("dummyJwtToken")
                .build();
    }

    // --- /api/auth/register ---

    @Test
    void register_ShouldReturnOkAndToken_WhenUserIsNotRegistered() throws Exception {
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authenticationResponse.getToken()));
    }

    @Test
    void register_ShouldReturnConflict_WhenUserIsAlreadyRegistered() throws Exception {
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyRegisteredException("User already registered"));


        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already registered"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .firstname("")
                .lastname("Doe")
                .email("invalid-email")
                .password("123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // --- /api/auth/login ---

    @Test
    void login_ShouldReturnOkAndToken_WhenCredentialsAreCorrect() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authenticationResponse.getToken()));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreIncorrect() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new WrongEmailOrPasswordException("Wrong email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Wrong email or password"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        AuthenticationRequest invalidRequest = AuthenticationRequest.builder()
                .email("not-an-email")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
