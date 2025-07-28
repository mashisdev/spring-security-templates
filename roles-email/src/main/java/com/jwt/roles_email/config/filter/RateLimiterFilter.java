package com.jwt.roles_email.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.roles_email.exception.ErrorMessage;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    public RateLimiterFilter(RateLimiterRegistry rateLimiterRegistry, ObjectMapper objectMapper) {
        this.rateLimiter = rateLimiterRegistry.rateLimiter("apiRateLimiter");
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            RateLimiter.decorateRunnable(rateLimiter, () -> {
                try {
                    filterChain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            }).run();
        } catch (RequestNotPermitted e) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String requestURI = request.getRequestURI();
            ErrorMessage errorMessage = new ErrorMessage(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    e,
                    "Too Many Requests. Please try again later.",
                    requestURI
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorMessage));
            response.getWriter().flush();
        }
    }
}