package com.revature.battlesimulator.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.revature.battlesimulator.utils.custom_exceptions.CharacterNotFoundException;
import com.revature.battlesimulator.utils.custom_exceptions.DuplicateCharacterNameException;
import com.revature.battlesimulator.utils.custom_exceptions.GameRoomNotFoundException;
import com.revature.battlesimulator.utils.custom_exceptions.InvalidInformationException;
import com.revature.battlesimulator.utils.custom_exceptions.OpenAIException;
import com.revature.battlesimulator.utils.custom_exceptions.UserNotFoundException;
import com.revature.battlesimulator.utils.custom_exceptions.InsufficientPermissionException;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(GameRoomNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleGameRoomNotFoundException(GameRoomNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CharacterNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUsernameException(CharacterNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DuplicateCharacterNameException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateCharacterNameException(
            DuplicateCharacterNameException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidInformationException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInformationException(InvalidInformationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OpenAIException.class)
    public ResponseEntity<Map<String, Object>> handleOpenAIException(OpenAIException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InsufficientPermissionException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientPermissionException(
            InsufficientPermissionException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.status(403).body(response);
    }
}
