package com.jwt.simple.exception;

import com.jwt.simple.exception.user.NotAllowedToChangeCredentialsException;
import com.jwt.simple.exception.user.UserAlreadyRegisteredException;
import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.exception.user.WrongEmailOrPasswordException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handles UserNotFoundException, returning 404 NOT FOUND
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        log.error("User not found: {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Handles UserAlreadyRegisteredException, returning 409 CONFLICT
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ErrorMessage> handleUserAlreadyRegistered(UserAlreadyRegisteredException ex, HttpServletRequest request) {
        log.error("User registration conflict: {} for email: {}", ex.getMessage(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(HttpStatus.CONFLICT.value(), ex, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Handles WrongEmailOrPasswordException, returning 401 UNAUTHORIZED
    @ExceptionHandler(WrongEmailOrPasswordException.class)
    public ResponseEntity<ErrorMessage> handleWrongEmailOrPassword(WrongEmailOrPasswordException ex, HttpServletRequest request) {
        log.warn("Authentication failed (wrong email/password): {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), ex, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Handles NotAllowedToChangeCredentialsException, returning 403 FORBIDDEN
    @ExceptionHandler(NotAllowedToChangeCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleNotAllowedToChangeCredentials(NotAllowedToChangeCredentialsException ex, HttpServletRequest request) {
        log.warn("Forbidden access (credentials change not allowed): {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(HttpStatus.FORBIDDEN.value(), ex, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Handles AuthenticationException (e.g., unauthenticated access, invalid JWT token), returning 401 UNAUTHORIZED
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication required or failed: {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                ex,
                "You are not authenticated. Please log in to access this resource.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Handles HttpMediaTypeNotSupportedException, returning 415 UNSUPPORTED MEDIA TYPE.
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String supportedMediaTypes = ex.getSupportedMediaTypes().stream()
                .map(mediaType -> "'" + mediaType.toString() + "'")
                .collect(Collectors.joining(", "));
        String message = String.format("Content type '%s' not supported. Supported media types are: %s",
                ex.getContentType(), supportedMediaTypes);
        log.warn("Unsupported Media Type: {} for path: {}. Supported types: {}", ex.getContentType(), request.getRequestURI(), supportedMediaTypes, ex);

        ErrorMessage error = new ErrorMessage(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ex,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    // Handles HttpRequestMethodNotSupportedException, returning 405 METHOD NOT ALLOWED
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not allowed: {} attempted on path: {}", ex.getMethod(), request.getRequestURI(), ex);
        String message = String.format("Method '%s' not allowed for this endpoint.", ex.getMethod());
        ErrorMessage error = new ErrorMessage(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                ex,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    // Groups various exceptions that typically result in a 400 BAD REQUEST status
    @ExceptionHandler({
            HttpMessageNotReadableException.class,         // Request body is missing or malformed
            MissingRequestHeaderException.class,           // Required HTTP header is missing
            MissingServletRequestParameterException.class, // Required query or form parameter is missing
            MethodArgumentTypeMismatchException.class      // Method argument cannot be converted to the expected type
    })
    public ResponseEntity<ErrorMessage> handleBadRequestExceptions(Exception ex, HttpServletRequest request) {
        String message = "The request could not be understood or was malformed.";
        String logMessage = "Bad request detected: {} for path: {}";

        if (ex instanceof HttpMessageNotReadableException) {
            message = "Required request body is missing or invalid. Please provide a valid JSON body.";
            logMessage = "HTTP message not readable: {} for path: {}";
        } else if (ex instanceof MissingRequestHeaderException missingHeaderEx) {
            message = String.format("Required header '%s' is missing.", missingHeaderEx.getHeaderName());
            logMessage = "Missing header '{}' for path: {}";
        } else if (ex instanceof MissingServletRequestParameterException missingParamEx) {
            message = String.format("Required parameter '%s' is missing.", missingParamEx.getParameterName());
            logMessage = "Missing parameter '{}' for path: {}";
        } else if (ex instanceof MethodArgumentTypeMismatchException mismatchEx) {
            message = String.format("Parameter '%s' has invalid type '%s'. Required type is '%s'.",
                    mismatchEx.getName(), mismatchEx.getValue(), mismatchEx.getRequiredType() != null ? mismatchEx.getRequiredType().getSimpleName() : "unknown");
            logMessage = "Method argument type mismatch: '{}' with value '{}' for path: {}";
        }

        log.warn(logMessage, ex.getMessage(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                ex,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handles NoResourceFoundException, returning 404 NOT FOUND
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessage> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} for path: {}", ex.getResourcePath(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                ex,
                "The requested resource was not found.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Handles JWT Exceptions
    @ExceptionHandler({
            MalformedJwtException.class,
            SignatureException.class,
            UnsupportedJwtException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorMessage> handleJwtExceptions(Exception ex, HttpServletRequest request) {
        String message;
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        switch (ex) {
            case MalformedJwtException malformedJwtException -> {
                message = "JWT Token is malformed or invalid. Ensure it is correctly formatted.";
                log.warn("Malformed JWT Token for path: {}. Error: {}", request.getRequestURI(), ex.getMessage(), ex);
            }
            case SignatureException signatureException -> {
                message = "JWT Token signature is invalid. Ensure the token has not been tampered with.";
                log.warn("Invalid JWT Signature for path: {}. Error: {}", request.getRequestURI(), ex.getMessage(), ex);
            }
            case UnsupportedJwtException unsupportedJwtException -> {
                message = "The JWT is not supported.";
                log.warn("Unsupported JWT Token for path: {}. Error: {}", request.getRequestURI(), ex.getMessage(), ex);
            }
            case IllegalArgumentException illegalArgumentException -> {
                message = "Illegal argument or empty JWT string.";
                log.warn("Illegal argument in JWT for path: {}. Error: {}", request.getRequestURI(), ex.getMessage(), ex);
            }
            default -> {
                message = "An unexpected JWT-related error occurred.";
                log.error("Unexpected JWT error for path: {}. Error: {}", request.getRequestURI(), ex.getMessage(), ex);
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        ErrorMessage error = new ErrorMessage(
                status.value(),
                ex,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    // Handler for @Valid validation errors (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed for path: {} with errors: {}", request.getRequestURI(), errors, ex);
        ErrorMessage error = new ErrorMessage(
                ex.getStatusCode().value(),
                ex,
                "Validation failed",
                request.getRequestURI(),
                errors
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // RateLimiter exception
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorMessage> handleRequestNotPermitted(RequestNotPermitted ex, HttpServletRequest request) {
        log.warn("Rate limit exceeded: {} for path: {}", ex.getMessage(), request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                ex,
                "Too many requests. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    // Catch-all handler for any unexpected exceptions, returning 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("An unexpected internal server error occurred for path: {}", request.getRequestURI(), ex);
        ErrorMessage error = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex, "An unexpected error occurred. Please try again later.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}