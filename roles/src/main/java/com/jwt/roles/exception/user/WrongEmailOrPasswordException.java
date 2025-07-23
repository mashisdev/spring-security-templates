package com.jwt.roles.exception.user;

public class WrongEmailOrPasswordException extends RuntimeException {
    public WrongEmailOrPasswordException(String message) { super(message); }
}
