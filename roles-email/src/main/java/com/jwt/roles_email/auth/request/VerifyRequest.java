package com.jwt.roles_email.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request body for account verification")
public record VerifyRequest (
    @NotBlank(message = "The email is mandatory")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "The email address to verify.", example = "john.doe@example.com")
    String email,

    @NotNull(message = "The verification code is mandatory")
    @Min(value = 100000, message = "Verification code must be a 6-digit number")
    @Max(value = 999999, message = "Verification code must be a 6-digit number")
    @Schema(description = "The 6-digit verification code sent to the user's email.", example = "123456")
    Integer verificationCode
){}
