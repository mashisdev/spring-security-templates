package com.jwt.simple.exception.user;

public class WrongEmailOrPasswordException extends RuntimeException {
    public WrongEmailOrPasswordException(String message) { super(message); }
}
