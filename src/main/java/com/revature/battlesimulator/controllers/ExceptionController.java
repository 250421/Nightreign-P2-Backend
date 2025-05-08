package com.revature.battlesimulator.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.revature.battlesimulator.utils.custom_exceptions.CharacterNotFoundException;
import com.revature.battlesimulator.utils.custom_exceptions.DuplicateCharacterNameException;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(CharacterNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUsernameException(CharacterNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DuplicateCharacterNameException.class)
    public ResponseEntity<Map<String, Object>> DuplicateCharacterNameException(DuplicateCharacterNameException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
