package com.jwt.roles_email.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest (
    @NotBlank(message = "The firstname is mandatory")
    String firstname,

    @NotBlank(message = "The lastname is mandatory")
    String lastname,

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The given email does not match the pattern of a valid email")
    String email,

    @NotBlank(message = "The password is mandatory")
    @Length(min = 6, message = "The password should be at least of 6 characters of length")
    String password
) {}

