package com.jwt.roles_email.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Request body for resetting password with a token")
public record ResetPasswordRequest(
        @NotBlank(message = "The token is mandatory")
        @Schema(description = "The password reset token received via email.", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @NotBlank(message = "The password is mandatory")
        @Length(min = 6, message = "The password should be at least of 6 characters of length")
        @Schema(description = "The new password for the user account.", example = "newPassword456")
        String password
) {}
