package com.revature.battlesimulator.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.battlesimulator.dtos.requests.NewCharacterRequest;
import com.revature.battlesimulator.dtos.requests.UpdateCharacterRequest;
import com.revature.battlesimulator.dtos.responses.UserSessionResponse;
import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.models.Role;
import com.revature.battlesimulator.services.CharacterService;
import com.revature.battlesimulator.services.SessionService;
import com.revature.battlesimulator.utils.custom_exceptions.InsufficientPermissionException;
import com.revature.battlesimulator.utils.custom_exceptions.UnauthenticatedUserException;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@AllArgsConstructor
@RequestMapping("/characters")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Data
public class CharacterController {
    private final CharacterService characterService;
    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<List<Character>> getAllCharacters() {
        return ResponseEntity.ok(characterService.getAllCharacters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(characterService.getCharacterById(id));
    }

    @PostMapping
    public ResponseEntity<Character> createCharacter(@RequestBody NewCharacterRequest newCharacterRequest) {
        verifyAdminAccess("create a character");

        Character newCharacter = characterService.createCharacter(newCharacterRequest);
        return ResponseEntity.ok(newCharacter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacter(@PathVariable Long id,
            @RequestBody UpdateCharacterRequest updateCharacterRequest) {
        verifyAdminAccess("update a character");
        Character updatedCharacter = characterService.updateCharacter(id, updateCharacterRequest);

        return ResponseEntity.ok(updatedCharacter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        verifyAdminAccess("delete a character");
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }

    private void verifyAdminAccess(String action) {
        UserSessionResponse currentUser = sessionService.getActiveUserSession();
        if (currentUser == null) {
            throw new UnauthenticatedUserException("You must be logged in to " + action);
        }
        if (currentUser.getRole() != Role.ADMIN) {
            throw new InsufficientPermissionException("You must be an admin to " + action);
        }
    }
}