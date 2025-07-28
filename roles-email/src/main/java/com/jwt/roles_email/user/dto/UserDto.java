package com.jwt.roles_email.user.dto;

import com.jwt.roles_email.user.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
        private Long id;
        private String firstname;
        private String lastname;
        private String email;
        @Enumerated(EnumType.STRING)
        private Role role;
}
