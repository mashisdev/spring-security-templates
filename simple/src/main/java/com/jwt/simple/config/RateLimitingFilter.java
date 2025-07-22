package com.jwt.simple.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.simple.exception.ErrorMessage;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter authRateLimiter;
    private final RateLimiter userRateLimiter;
    private final ObjectMapper objectMapper;

    // Inyecta RateLimiterRegistry y ObjectMapper
    public RateLimitingFilter(RateLimiterRegistry rateLimiterRegistry, ObjectMapper objectMapper) {
        // Obtiene los RateLimiters configurados por nombre desde application.yml
        this.authRateLimiter = rateLimiterRegistry.rateLimiter("authRateLimiterFilter");
        this.userRateLimiter = rateLimiterRegistry.rateLimiter("userRateLimiterFilter");
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        RateLimiter applicableRateLimiter = null;
        String rateLimiterName = "";

        // Define qué RateLimiter aplicar según la ruta de la solicitud
        if (requestURI.startsWith("/api/auth/register") || requestURI.startsWith("/api/auth/login")) {
            applicableRateLimiter = authRateLimiter;
            rateLimiterName = "authRateLimiterFilter";
        } else if (requestURI.startsWith("/api/users")) {
            applicableRateLimiter = userRateLimiter;
            rateLimiterName = "userRateLimiterFilter";
        }

        if (applicableRateLimiter != null) {
            try {
                // Intenta adquirir un permiso del RateLimiter seleccionado
                applicableRateLimiter.acquirePermission();
                log.debug("RateLimiter '{}' permit acquired for URI: {}", rateLimiterName, requestURI);
                // Si se adquiere el permiso, la cadena de filtros continúa
                filterChain.doFilter(request, response);
            } catch (RequestNotPermitted ex) {
                // Si el RateLimiter no permite la solicitud (límite excedido)
                log.warn("Rate limit exceeded for URI: {} using RateLimiter '{}'. Message: {}", requestURI, rateLimiterName, ex.getMessage());
                // Envía la respuesta de error directamente al cliente
                sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Please try again later.", ex, requestURI);
                // ¡Importante! No llamar a filterChain.doFilter() aquí para detener el procesamiento
            }
        } else {
            // Para rutas que no están cubiertas por la lógica de limitación de tasa del filtro,
            // simplemente continúa la cadena de filtros normalmente.
            filterChain.doFilter(request, response);
        }
    }

    // Método auxiliar para construir y enviar la respuesta de error JSON
    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message, Throwable ex, String path) throws IOException {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Utiliza tu clase ErrorMessage para el formato de la respuesta
        ErrorMessage error = new ErrorMessage(httpStatus.value(), (Exception) ex, message, path);
        response.getWriter().write(objectMapper.writeValueAsString(error));
        response.getWriter().flush();
    }
}