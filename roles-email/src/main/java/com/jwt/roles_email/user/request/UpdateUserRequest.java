package com.jwt.roles_email.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest (

    @NotNull(message = "The id cannot be null")
    Long id,

    @NotBlank(message = "The firstname is mandatory")
    String firstname,

    @NotBlank(message = "The lastname is mandatory")
    String lastname,

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The given email does not match the pattern of a valid email")
    String email
) {}
