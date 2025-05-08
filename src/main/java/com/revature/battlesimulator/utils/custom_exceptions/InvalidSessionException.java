package com.revature.battlesimulator.utils.custom_exceptions;

public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException(String message) {
        super(message);
    }
}
