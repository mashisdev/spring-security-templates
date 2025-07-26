package com.jwt.roles_email.user.dto;

import com.jwt.roles_email.user.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record UserDto (
        Long id,
        String firstname,
        String lastname,
        String email,
        @Enumerated(EnumType.STRING)
        Role role
) {}
