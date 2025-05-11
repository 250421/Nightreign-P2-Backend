// LobbyService.java
package com.revature.battlesimulator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.revature.battlesimulator.dtos.responses.UserSessionResponse;
import com.revature.battlesimulator.models.GameLobbyUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LobbyService {
    // Messaging template to broadcast updates
    private final SimpMessagingTemplate messagingTemplate;
    // Session service to get current user
    private final SessionService sessionService;

    // Store of online users (userId -> user)
    private final Map<Long, GameLobbyUser> onlineUsers = new ConcurrentHashMap<>();

    /**
     * Called when a user enters the lobby
     */
    public void enterLobby() {
        // Get the current logged-in user
        UserSessionResponse currentUser = sessionService.getActiveUserSession();
        if (currentUser == null) {
            return; // Not logged in
        }

        // Create lobby user object
        GameLobbyUser lobbyUser = new GameLobbyUser(
                currentUser.getId(),
                currentUser.getUsername());

        // Add to online users map
        onlineUsers.put(currentUser.getId(), lobbyUser);

        // Send update to all clients
        broadcastLobbyUpdate();
    }

    /**
     * Called when a user leaves the lobby
     */
    public void leaveLobby() {
        // Get the current logged-in user
        UserSessionResponse currentUser = sessionService.getActiveUserSession();
        if (currentUser == null) {
            return; // Not logged in
        }

        // Remove from online users
        onlineUsers.remove(currentUser.getId());

        // Send update to all clients
        broadcastLobbyUpdate();
    }

    /**
     * Broadcasts the current lobby state to all connected clients
     */
    private void broadcastLobbyUpdate() {
        // Get list of all online users
        List<GameLobbyUser> userList = new ArrayList<>(onlineUsers.values());

        // Send to the "/topic/lobby" destination
        messagingTemplate.convertAndSend("/topic/lobby", userList);
    }

    /**
     * Get all currently online users
     */
    public List<GameLobbyUser> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }
}