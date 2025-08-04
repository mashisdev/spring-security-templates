package com.jwt.roles_email.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for password reset request")
public record RedeemPasswordRequest(
        @NotBlank(message = "The email is mandatory")
        @Email(message = "The given email does not match the pattern of a valid email")
        @Schema(description = "The email address for which to reset the password.", example = "john.doe@example.com")
        String email
) {}
