package com.jwt.roles_email.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record AuthenticationRequest (
        @NotEmpty(message = "The email is mandatory")
        @Email(message = "The given email does not match the pattern")
        String email,
        @NotEmpty(message = "The password is mandatory")
        @Length(min = 6, message = "The password should be at least of 6 characters of length")
        String password
) {}