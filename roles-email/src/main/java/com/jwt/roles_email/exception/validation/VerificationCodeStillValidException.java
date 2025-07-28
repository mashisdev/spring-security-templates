package com.jwt.roles_email.exception.validation;

public class VerificationCodeStillValidException extends RuntimeException {
    public VerificationCodeStillValidException(String message) {
        super(message);
    }
}
