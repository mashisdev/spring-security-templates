package com.jwt.roles_email.exception.user;

public class NotAllowedToChangeCredentialsException extends RuntimeException {
    public NotAllowedToChangeCredentialsException(String message) {
        super(message);
    }
}
