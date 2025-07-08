package com.jwt.simple.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "The firstname is mandatory")
    private String firstname;

    @NotBlank(message = "The lastname is mandatory")
    private String lastname;

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The given email does not match the pattern of a valid email")
    private String email;

    @NotBlank(message = "The password is mandatory")
    @Length(min = 6, message = "The password should be at least of 6 characters of length")
    private String password;
}

