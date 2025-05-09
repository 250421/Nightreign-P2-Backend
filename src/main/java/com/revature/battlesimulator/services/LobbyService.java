package com.revature.battlesimulator.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.revature.battlesimulator.dtos.requests.LobbyStatusUpdate;
import com.revature.battlesimulator.dtos.responses.UserSessionResponse;
import com.revature.battlesimulator.models.GameLobbyUser;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.models.GameStatus;
import com.revature.battlesimulator.models.UserStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LobbyService {
    private final SimpMessagingTemplate messagingTemplate;
    private final SessionService sessionService;

    // In-memory store of online users and game rooms
    private final Map<Long, GameLobbyUser> onlineUsers = new ConcurrentHashMap<>();
    private final Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>();

    public void userJoinedLobby(Long userId) {
        UserSessionResponse userSession = sessionService.getActiveUserSession();
        if (userSession == null)
            return;

        GameLobbyUser lobbyUser = new GameLobbyUser();
        lobbyUser.setUserId(userId);
        lobbyUser.setUsername(userSession.getUsername());
        lobbyUser.setStatus(UserStatus.ONLINE);
        lobbyUser.setJoinedAt(LocalDateTime.now());

        onlineUsers.put(userId, lobbyUser);

        // Broadcast updated lobby state to all connected clients
        broadcastLobbyUpdate();
    }

    private void broadcastLobbyUpdate() {
        LobbyStatusUpdate update = new LobbyStatusUpdate();
        update.setOnlineUsers(new ArrayList<>(onlineUsers.values()));
        update.setGameRooms(new ArrayList<>(gameRooms.values()));

        messagingTemplate.convertAndSend("/topic/lobby", update);
    }

    public void userLeftLobby(Long userId) {
        onlineUsers.remove(userId);
        // Remove user from any game rooms
        gameRooms.values().stream()
                .filter(room -> isUserInRoom(userId, room))
                .forEach(room -> handleUserLeaveRoom(userId, room.getRoomId()));

        broadcastLobbyUpdate();
    }

    private boolean isUserInRoom(Long userId, GameRoom room) {
        return (room.getPlayer1() != null && room.getPlayer1().getUserId().equals(userId)) ||
                (room.getPlayer2() != null && room.getPlayer2().getUserId().equals(userId));
    }

    private void handleUserLeaveRoom(Long userId, String roomId) {
        GameRoom room = gameRooms.get(roomId);
        if (room == null)
            return;

        if (room.getPlayer1() != null && room.getPlayer1().getUserId().equals(userId)) {
            if (room.getPlayer2() == null) {
                // If creator leaves and no one else is in room, delete room
                gameRooms.remove(roomId);
            } else {
                // Make player2 the new player1
                room.setPlayer1(room.getPlayer2());
                room.setPlayer2(null);
                room.setStatus(GameStatus.WAITING_FOR_PLAYERS);
            }
        } else if (room.getPlayer2() != null && room.getPlayer2().getUserId().equals(userId)) {
            room.setPlayer2(null);
            room.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        }

        // Update leaving user's status if they're still online
        GameLobbyUser user = onlineUsers.get(userId);
        if (user != null) {
            user.setStatus(UserStatus.ONLINE);
        }
    }

}