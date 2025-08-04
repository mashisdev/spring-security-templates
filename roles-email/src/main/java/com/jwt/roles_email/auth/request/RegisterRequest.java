package com.jwt.roles_email.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Request body for user registration")
public record RegisterRequest (
    @NotBlank(message = "The firstname is mandatory")
    @Schema(description = "The first name of the user.", example = "John")
    String firstname,

    @NotBlank(message = "The lastname is mandatory")
    @Schema(description = "The last name of the user.", example = "Doe")
    String lastname,

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The given email does not match the pattern of a valid email")
    @Schema(description = "The email address of the user.", example = "john.doe@example.com")
    String email,

    @NotBlank(message = "The password is mandatory")
    @Length(min = 6, message = "The password should be at least of 6 characters of length")
    @Schema(description = "The password for the user account. Must be at least 6 characters long.", example = "password123")
    String password
) {}

