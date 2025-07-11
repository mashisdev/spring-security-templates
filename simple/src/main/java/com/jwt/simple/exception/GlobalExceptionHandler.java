package com.jwt.simple.exception;

import com.jwt.simple.exception.user.NotAllowedToChangeCredentialsException;
import com.jwt.simple.exception.user.UserAlreadyRegisteredException;
import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.exception.user.WrongEmailOrPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // User Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("auth", ex.getMessage()));
    }

    // Registration
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyRegistered(UserAlreadyRegisteredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("auth", ex.getMessage()));
    }

    // Login
    @ExceptionHandler(WrongEmailOrPasswordException.class)
    public ResponseEntity<Map<String, String>> handleWrongEmailOrPassword(WrongEmailOrPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("auth", ex.getMessage()));
    }

    // Not Allowed To Change Credentials
    @ExceptionHandler(NotAllowedToChangeCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleNotAllowedToChangeCredentials(NotAllowedToChangeCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("auth", ex.getMessage()));
    }

    // Method Not Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(ex.getStatusCode()).body(errors);
    }
}
