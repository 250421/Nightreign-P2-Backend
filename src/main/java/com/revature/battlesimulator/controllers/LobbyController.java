// LobbyController.java
package com.revature.battlesimulator.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.revature.battlesimulator.models.GameLobbyUser;
import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.services.LobbyService;
import com.revature.battlesimulator.services.UserService;
import com.revature.battlesimulator.utils.custom_exceptions.UserNotFoundException;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class LobbyController {
    private final LobbyService lobbyService;
    private final UserService userService;

    // WebSocket endpoint for entering the lobby
    @MessageMapping("/lobby/enter")
    public void enterLobby() {
        // Simply delegate to the service
        lobbyService.enterLobby();
    }

    @PostMapping("/lobby/enter")
    @ResponseBody
    public ResponseEntity<String> enterLobbyRest() {
        lobbyService.enterLobby();
        return ResponseEntity.ok("Entered lobby successfully");
    }

    // WebSocket endpoint for leaving the lobby
    @MessageMapping("/lobby/leave")
    public void leaveLobby() {
        // Simply delegate to the service
        lobbyService.leaveLobby();
    }

    @PostMapping("/lobby/leave")
    @ResponseBody
    public ResponseEntity<String> leaveLobbyRest() {
        lobbyService.leaveLobby();
        return ResponseEntity.ok("Left lobby successfully");
    }

    // REST endpoint to get initial state
    @GetMapping("/lobby/users")
    @ResponseBody
    public List<GameLobbyUser> getOnlineUsers() {
        return lobbyService.getOnlineUsers();
    }

    @GetMapping("/auth/lobby/participants")
    @ResponseBody
    public ResponseEntity<List<GameLobbyUser>> getParticipants(@RequestParam String roomId) {
        List<User> participants = userService.findByRoomId(roomId);
        if(participants.isEmpty()) {
            throw new UserNotFoundException("Room doesnt exist or currently has no participants.");
        }
        //convert to gameLobbyUser
        List<GameLobbyUser> gameLobbyParticipants = participants.stream()
        .map(user -> new GameLobbyUser(user.getId(), user.getUsername()))
        .collect(Collectors.toList());

        return ResponseEntity.status(200).body(gameLobbyParticipants);
    }
}