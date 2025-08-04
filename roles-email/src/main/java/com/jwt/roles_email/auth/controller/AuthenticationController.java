package com.jwt.roles_email.auth.controller;

import com.jwt.roles_email.auth.request.*;
import com.jwt.roles_email.auth.response.AuthenticationResponse;
import com.jwt.roles_email.exception.ErrorMessage;
import com.jwt.roles_email.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Tag(name = "Authentication Management", description = "APIs for user registration, login, and password management.")
public interface AuthenticationController {

    @Operation(summary = "Register a new user", description = "Creates a new user and sends a verification code to their email.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. The request body is invalid or has validation errors.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict. A user with the same email already exists.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<UserDto> register(RegisterRequest request);

    @Operation(summary = "Verify user account", description = "Verifies a user's account using the code sent to their email.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account verified successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"verification\": \"Account verified successfully\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. Invalid verification code or request body.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict. Account is already verified.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Gone. Verification code has expired.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<Map<String, String>> verify(VerifyRequest verifyRequest);

    @Operation(summary = "Resend verification code", description = "Sends a new verification code to the user's email if the previous one has expired.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification code resent successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"verification\": \"Verification code sent\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. The request body is invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict. A valid verification code already exists. Please wait to request a new one.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<Map<String, String>> resendVerificationCode(VerifyRequest verifyRequest);

    @Operation(summary = "Log in a user", description = "Authenticates a user with email and password and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. The request body is invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Invalid email or password.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. Account is not verified.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests. Rate limit exceeded.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request);

    @Operation(summary = "Request password reset", description = "Sends a password reset link to the user's email.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset link sent successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Send the redeem password link to your email\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. The request body is invalid.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. User not found.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<Map<String, String>> redeemPassword(RedeemPasswordRequest request);

    @Operation(summary = "Reset password", description = "Resets the user's password using a valid token from the reset link.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Credentials updated\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request. The request body is invalid or the token is incorrect.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Gone. Password reset token has expired.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    ResponseEntity<Map<String, String>> resetPassword(ResetPasswordRequest request);
}
