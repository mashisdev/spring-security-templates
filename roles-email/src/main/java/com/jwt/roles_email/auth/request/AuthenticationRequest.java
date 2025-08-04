package com.jwt.roles_email.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Request body for user login")
public record AuthenticationRequest (
        @NotEmpty(message = "The email is mandatory")
        @Email(message = "The given email does not match the pattern")
        @Schema(description = "The email address of the user.", example = "john.doe@example.com")
        String email,

        @NotEmpty(message = "The password is mandatory")
        @Length(min = 6, message = "The password should be at least of 6 characters of length")
        @Schema(description = "The password for the user account.", example = "password123")
        String password
) {}