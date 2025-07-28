package com.jwt.roles_email.user.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    private boolean enabled;
    private LocalDateTime verificationCodeExpiresAt;
    private Integer verificationCode;
    private String resetToken;
    private Instant resetTokenExpiration;
}
