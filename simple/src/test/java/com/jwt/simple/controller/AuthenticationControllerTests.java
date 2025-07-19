package com.jwt.simple.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.simple.auth.AuthenticationController;
import com.jwt.simple.auth.AuthenticationService;
import com.jwt.simple.auth.request.AuthenticationRequest;
import com.jwt.simple.auth.request.RegisterRequest;
import com.jwt.simple.auth.response.AuthenticationResponse;
import com.jwt.simple.exception.GlobalExceptionHandler;
import com.jwt.simple.exception.user.UserAlreadyRegisteredException;
import com.jwt.simple.exception.user.WrongEmailOrPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTests {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(this.authenticationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // --- Register endpoint tests ---

    @Test
    void shouldRegisterUserAndReturnToken_whenValidRegisterRequest() throws Exception {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .build();
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .token("jwt-token-abcd")
                .build();

        // When
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(expectedResponse);

        // Perform the POST request to the register endpoint
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.token").value("jwt-token-abcd")); // Verify the token in the response

        // Verify that the register method of the authentication service was called exactly once
        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnConflict_whenRegisterWithAlreadyRegisteredEmail() throws Exception {
        // Given a registration request with an email that already exists
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("existing@example.com")
                .password("password123")
                .build();

        // When the authentication service is called, throw UserAlreadyRegisteredException
        doThrow(new UserAlreadyRegisteredException("User already registered"))
                .when(authenticationService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.authentication").value("User already registered")); // Verify the error message

        verify(authenticationService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterRequestHasInvalidFields() throws Exception {
        // Given a registration request with multiple invalid fields
        RegisterRequest request = RegisterRequest.builder()
                .firstname("") // @NotBlank, will fail
                .lastname(null) // @NotBlank, will fail
                .email("invalid-email") // @Email, will fail
                .password("short") // @Length(min=6), will fail
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // Verify specific error messages for each field from GlobalExceptionHandler
                .andExpect(jsonPath("$.firstname").value("The firstname is mandatory"))
                .andExpect(jsonPath("$.lastname").value("The lastname is mandatory"))
                .andExpect(jsonPath("$.email").value("The given email does not match the pattern of a valid email"))
                .andExpect(jsonPath("$.password").value("The password should be at least of 6 characters of length"));

        // Verify that the authentication service was NEVER called
        verify(authenticationService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterRequestHasMissingPassword() throws Exception {
        // Given a registration request with a missing password
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Valid")
                .lastname("User")
                .email("valid@example.com")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("The password is mandatory"));

        verify(authenticationService, never()).register(any(RegisterRequest.class));
    }

    // --- Login endpoint tests ---

    @Test
    void shouldAuthenticateUserAndReturnToken_whenValidLoginRequest() throws Exception {
        // Given a valid authentication request
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("user@example.com")
                .password("correctpassword")
                .build();

        // Given an expected authentication response
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .token("jwt-token-xyz")
                .build();

        // When the authentication service is called, return the expected response
        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.token").value("jwt-token-xyz")); // Verify the token in the response

        // Verify that the authenticate method of the authentication service was called exactly once
        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void shouldReturnUnauthorized_whenLoginWithWrongEmailOrPassword() throws Exception {
        // Given an authentication request with wrong credentials
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("user@example.com")
                .password("wrongpassword")
                .build();

        // When the authentication service is called, throw WrongEmailOrPasswordException
        doThrow(new WrongEmailOrPasswordException("Wrong email or password"))
                .when(authenticationService).authenticate(any(AuthenticationRequest.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()) // Expect HTTP 401 Unauthorized
                .andExpect(jsonPath("$.authentication").value("Wrong email or password"));

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void shouldReturnBadRequest_whenLoginRequestHasInvalidFields() throws Exception {
        // Given a login request with invalid fields
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("bad-email") // @Email
                .password("short") // @Length(min=6)
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // Verify specific error messages for each field
                .andExpect(jsonPath("$.email").value("The given email does not match the pattern"))
                .andExpect(jsonPath("$.password").value("The password should be at least of 6 characters of length"));

        // Verify that the authentication service was NEVER called
        verify(authenticationService, never()).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void shouldReturnBadRequest_whenLoginRequestHasMissingEmail() throws Exception {
        // Given a login request with a missing email
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("") // @NotEmpty
                .password("validpassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("The email is mandatory"));

        verify(authenticationService, never()).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void shouldReturnBadRequest_whenLoginRequestHasMissingPassword() throws Exception {
        // Given a login request with a missing password
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("valid@example.com")
                .password("") // @NotEmpty
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("The password is mandatory"));

        verify(authenticationService, never()).authenticate(any(AuthenticationRequest.class));
    }
}
