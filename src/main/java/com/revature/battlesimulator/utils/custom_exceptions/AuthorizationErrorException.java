package com.revature.battlesimulator.utils.custom_exceptions;

public class AuthorizationErrorException extends RuntimeException {
    public AuthorizationErrorException(String message) {
        super(message);
    }
}
