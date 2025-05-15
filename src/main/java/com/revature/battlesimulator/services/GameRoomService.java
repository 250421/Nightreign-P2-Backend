// LobbyService.java
package com.revature.battlesimulator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.revature.battlesimulator.models.GameRoomUser;
import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.models.GameRoom;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>();

    public GameRoom createRoom(String roomName, Long userId, String username) {
        GameRoomUser creator = new GameRoomUser(userId, username);
        GameRoom room = new GameRoom(roomName, creator);
        gameRooms.put(room.getId(), room);
        broadcastRoomsList();
        return room;
    }

    public boolean joinRoom(String roomId, Long userId, String username) {
        GameRoom room = gameRooms.get(roomId);
        if (room == null) {
            return false;
        }
        GameRoomUser user = new GameRoomUser(userId, username);
        boolean joined = room.addPlayer(user);
        if (joined) {
            updateRoomStatus(roomId);
            broadcastRoomsList();
            messagingTemplate.convertAndSend("/topic/room/" + roomId, room);
        }
        return joined;
    }

    public boolean leaveRoom(String roomId, Long userId) {
        GameRoom room = gameRooms.get(roomId);
        if (room == null) {
            return false;
        }
        boolean left = room.removePlayer(userId);
        if (room.getPlayers().isEmpty()) {
            gameRooms.remove(roomId);
        } else {
            updateRoomStatus(roomId);
        }
        broadcastRoomsList();
        if (gameRooms.containsKey(roomId)) {
            messagingTemplate.convertAndSend("/topic/room/" + roomId, room);
        }
        return left;
    }

    public GameRoomUser getUserFromRoom(String roomId, Long userId) {
        GameRoom room = gameRooms.get(roomId);
        if (room == null) {
            return null;
        }
        for (GameRoomUser player : room.getPlayers()) {
            if (player.getUserId().equals(userId)) {
                return player;
            }
        }
        return null;
    }

    public List<GameRoom> getAllRooms() {
        return new ArrayList<>(gameRooms.values());
    }

    public GameRoom getRoomById(String roomId) {
        return gameRooms.get(roomId);
    }

    public void updatePlayer(String roomId, Long userId, List<Character> activeCharacters,
            boolean isReadyForBattle) {
        GameRoom room = gameRooms.get(roomId);
        if (room == null) {
            return;
        }
        GameRoomUser player = getUserFromRoom(roomId, userId);
        if (player != null) {
            player.setActiveCharacters(activeCharacters);
            player.setReadyForBattle(isReadyForBattle);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, room);
            updateRoomStatus(roomId);
        }
    }

    public void updateRoomStatus(String roomId) {
        GameRoom room = gameRooms.get(roomId);
        if (room == null) {
            return;
        }
        if (room.getPlayers().size() >= 2) {
            GameRoomUser player1 = room.getPlayers().get(0);
            GameRoomUser player2 = room.getPlayers().get(1);
            if (player1.isReadyForBattle() && player2.isReadyForBattle()) {
                room.setStatus(GameRoom.RoomStatus.IN_BATTLE);
            } else {
                room.setStatus(GameRoom.RoomStatus.CHOOSING_CHARACTERS);
            }
        } else if (room.getPlayers().size() == 1) {
            room.setStatus(GameRoom.RoomStatus.WAITING_FOR_PLAYERS);
        }

        messagingTemplate.convertAndSend("/topic/room/" + roomId, room);
    }

    private void broadcastRoomsList() {
        messagingTemplate.convertAndSend("/topic/rooms", getAllRooms());
    }

}