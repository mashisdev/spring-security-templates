package com.jwt.roles_email.auth.request;

import jakarta.validation.constraints.*;

public record VerifyRequest (
    @NotBlank(message = "The email is mandatory")
    @Email(message = "Please provide a valid email address")
    String email,

    @NotNull(message = "The verification code is mandatory")
    @Min(value = 100000, message = "Verification code must be a 6-digit number")
    @Max(value = 999999, message = "Verification code must be a 6-digit number")
//    @JsonProperty("verification_code")
    Integer verificationCode
){}
