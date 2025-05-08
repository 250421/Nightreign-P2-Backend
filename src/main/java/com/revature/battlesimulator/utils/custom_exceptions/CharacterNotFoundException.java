package com.revature.battlesimulator.utils.custom_exceptions;

public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException(String message) {
        super(message);
    }
}
