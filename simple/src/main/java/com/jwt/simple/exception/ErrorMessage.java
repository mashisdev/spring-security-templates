package com.jwt.simple.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    private LocalDateTime timestamp;
    private Integer status;
    private String exception;
    private String message;
    private String path;
    private Map<String, String> validationErrors;

    public ErrorMessage(Integer status, Exception ex, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.exception = ex.getClass().getSimpleName();
        this.message = message;
        this.path = path;
        this.validationErrors = null;
    }

    public ErrorMessage(Integer status, Exception ex, String message, String path, Map<String, String> validationErrors) {
        this(status, ex, message, path);
        this.validationErrors = validationErrors;
    }
}

