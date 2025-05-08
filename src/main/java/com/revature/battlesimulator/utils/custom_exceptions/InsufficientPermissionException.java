package com.revature.battlesimulator.utils.custom_exceptions;

public class InsufficientPermissionException extends RuntimeException {
    public InsufficientPermissionException(String message) {
        super(message);
    }
}
