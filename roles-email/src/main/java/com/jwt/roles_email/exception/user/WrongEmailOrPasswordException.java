package com.jwt.roles_email.exception.user;

public class WrongEmailOrPasswordException extends RuntimeException {
    public WrongEmailOrPasswordException(String message) { super(message); }
}
