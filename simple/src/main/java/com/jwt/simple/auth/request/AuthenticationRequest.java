package com.jwt.simple.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotEmpty(message = "The email is mandatory")
    @Email(message = "The given email does not match the pattern")
    private String email;
    @NotEmpty(message = "The password is mandatory")
    @Length(min = 5, message = "The password should be at least of 5 characters of length")
    private String password;
}

