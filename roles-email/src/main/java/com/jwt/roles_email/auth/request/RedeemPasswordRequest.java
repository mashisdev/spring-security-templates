package com.jwt.roles_email.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RedeemPasswordRequest(
        @NotBlank(message = "The email is mandatory")
        @Email(message = "The given email does not match the pattern of a valid email")
        String email
) {}
