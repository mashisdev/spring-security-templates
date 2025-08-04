package com.jwt.roles_email.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating user details")
public record UpdateUserRequest (

    @NotNull(message = "The id cannot be null")
    @Schema(description = "The unique ID of the user to update.", example = "1")
    Long id,

    @NotBlank(message = "The firstname is mandatory")
    @Schema(description = "The updated first name of the user.", example = "Jane")
    String firstname,

    @NotBlank(message = "The lastname is mandatory")
    @Schema(description = "The updated last name of the user.", example = "Smith")
    String lastname,

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The given email does not match the pattern of a valid email")
    @Schema(description = "The updated email address of the user.", example = "jane.smith@example.com")
    String email
) {}
