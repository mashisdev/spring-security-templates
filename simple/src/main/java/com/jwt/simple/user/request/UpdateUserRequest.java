package com.jwt.simple.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @NotNull(message = "The id cannot be null")
    private Long id;

    @NotBlank(message = "The firstname is mandatory")
    private String firstname;

    @NotBlank(message = "The lastname is mandatory")
    private String lastname;

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The given email does not match the pattern of a valid email")
    private String email;
}
