package com.jwt.simple.exception.user;

public class NotAllowedToChangeCredentialsException extends RuntimeException {
    public NotAllowedToChangeCredentialsException(String message) {
        super(message);
    }
}
