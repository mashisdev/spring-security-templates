package com.jwt.simple.exception;

import com.jwt.simple.exception.user.UserAlreadyExistException;
import com.jwt.simple.exception.user.UserNotFoundException;
import com.jwt.simple.exception.user.WrongEmailOrPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // User Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("user", ex.getMessage()));
    }

    // Registration
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(UserAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("user", ex.getMessage()));
    }

    // Login
    @ExceptionHandler(WrongEmailOrPasswordException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(WrongEmailOrPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("user", ex.getMessage()));
    }
}
