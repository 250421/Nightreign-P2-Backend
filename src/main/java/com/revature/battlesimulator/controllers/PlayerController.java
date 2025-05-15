package com.revature.battlesimulator.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.battlesimulator.dtos.requests.SaveCharacterRequest;
import com.revature.battlesimulator.dtos.responses.ErrorResponse;
import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.models.Player;
import com.revature.battlesimulator.services.CharacterService;
import com.revature.battlesimulator.services.PlayerService;
import com.revature.battlesimulator.services.UserService;
import com.revature.battlesimulator.utils.custom_exceptions.UserNotFoundException;
import com.revature.battlesimulator.utils.custom_exceptions.CharacterNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Data
public class PlayerController {

    private final PlayerService playerService;
    private final UserService userService;
    private final CharacterService characterService;
    
    @PostMapping("/players/selectChar")
    @ResponseBody
    public ResponseEntity<?> selectCharacter(@RequestBody SaveCharacterRequest saveCharacterRequest){
        User user = userService.findByUserId(saveCharacterRequest.getUserId());
        Character c = characterService.getCharacterById(saveCharacterRequest.getCharId());
        if(user == null || c == null) {
            return ResponseEntity.status(401).build();
        }
        boolean alreadySaved = playerService.existsByUserAndCharacter(user, c);
        if(alreadySaved) {
            ErrorResponse e = new ErrorResponse("Character has already been selected");
            return ResponseEntity.status(409).body(e);
        }

        Player p = new Player();
        p.setCharacter(c);
        p.setUser(user);
        p.setCharIsActive(true);
        playerService.selectChar(p);
        
        ErrorResponse e = new ErrorResponse("Successfully selected character");
        return ResponseEntity.status(200).body(e);
    }

    @GetMapping("/players/activeChars")
    @ResponseBody
    public ResponseEntity<?> getActiveChars(@RequestParam int userid) {
        User user = userService.findByUserId((long) userid);
        if (user == null) {
            throw new UserNotFoundException("User with id "+ userid + " not found");
        }
        List<Character> activeCharacters = playerService.findCharactersByUserAndCharIsActiveTrue(user);

        return ResponseEntity.status(200).body(activeCharacters);
    }

    @GetMapping("/players/defeatedChars")
    @ResponseBody
    public ResponseEntity<?> getDefeatedChars(@RequestParam int userid) {
        User user = userService.findByUserId((long) userid);
        if (user == null) {
            throw new UserNotFoundException("User with id "+ userid + " not found");
        }
        List<Character> defeatedChars = playerService.findCharactersByUserAndCharIsActiveFalse(user);

        return ResponseEntity.status(200).body(defeatedChars);
    }

    @PatchMapping("/players/disableChar")
    @ResponseBody
    public ResponseEntity<?> disableChar(@RequestBody SaveCharacterRequest saveCharacterRequest) {
        if(userService.findByUserId(saveCharacterRequest.getUserId()) == null) {
            throw new UserNotFoundException("User with id "+ saveCharacterRequest.getUserId() + " not found");
        }
        if(characterService.getCharacterById(saveCharacterRequest.getCharId()) == null) {
            throw new CharacterNotFoundException("Character with id "+ saveCharacterRequest.getCharId() + " not found");
        }
        playerService.disableChar(saveCharacterRequest.getUserId(),(int) saveCharacterRequest.getCharId());
        ErrorResponse e = new ErrorResponse("Successfully disabled character");
        return ResponseEntity.status(200).body(e);
    }

    @DeleteMapping("/players/deleteChars")
    @ResponseBody
    public ResponseEntity<?> deleteChars(@RequestParam int userid) {
        if(userService.findByUserId(userid) == null) {
            throw new UserNotFoundException("User with id "+ userid + " not found");
        }

        playerService.deleteChars(userid);
        ErrorResponse e = new ErrorResponse("Successfully deleted characters");
        return ResponseEntity.status(200).body(e);
    }
}
