package com.revature.battlesimulator.utils.custom_exceptions;

public class DuplicateCharacterNameException extends RuntimeException {
    public DuplicateCharacterNameException(String message) {
        super(message);
    }
}
