package com.jwt.roles_email.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Represents a successful authentication response.")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @Schema(description = "The JWT token for the authenticated user.", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}