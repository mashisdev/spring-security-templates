package com.jwt.simple.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info =@Info(
                title = "Simple JWT",
                description = "Simple JWT authentication API",
                version = "1.0.0",
                contact = @Contact(
                        name = "mashisdev", url = "https://mashisdev.github.io/"
                )
        ),
        servers = @Server(
                description = "Development",
                url = "http://localhost:8080"
        ),
        security = @SecurityRequirement(
                name = "Bearer Authentication"
        )
)
@SecurityScheme(
        name="Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        paramName = HttpHeaders.AUTHORIZATION,
        bearerFormat = "JWT",
        scheme = "bearer"

)
public class SwaggerConfig {
}
