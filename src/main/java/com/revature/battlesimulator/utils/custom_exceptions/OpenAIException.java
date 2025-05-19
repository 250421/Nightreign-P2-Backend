package com.revature.battlesimulator.utils.custom_exceptions;

public class OpenAIException extends RuntimeException {
    public OpenAIException(String message) {
        super("Error with OpenAI: " + message);
    }

    public OpenAIException(String message, Throwable cause) {
        super("Error with OpenAI: " + message, cause);
    }
    
}
