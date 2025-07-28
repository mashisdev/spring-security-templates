package com.jwt.roles_email.auth.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ResetPasswordRequest(
        @NotBlank(message = "The token is mandatory")
        String token,

        @NotBlank(message = "The password is mandatory")
        @Length(min = 6, message = "The password should be at least of 6 characters of length")
        String password
) {}
