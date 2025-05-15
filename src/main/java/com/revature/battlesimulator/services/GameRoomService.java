// LobbyService.java
package com.revature.battlesimulator.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.revature.battlesimulator.models.GameRoomUser;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.models.Character;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameRoomService {
    private final SimpMessagingTemplate messagingTemplate;
    private final CharacterService characterService;

    private final Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>();

    public GameRoom createRoom(String roomName, Long userId, String username) {
        GameRoomUser creator = new GameRoomUser(userId, username);
        Character char1 = characterService.getCharacterById(2L);
        Character char2 = characterService.getCharacterById(4L);
        Character char3 = characterService.getCharacterById(21L);
        creator.getActiveCharacters().add(char1);
        creator.getActiveCharacters().add(char2);
        creator.getActiveCharacters().add(char3);
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
        Character char1 = characterService.getCharacterById(2L);
        Character char2 = characterService.getCharacterById(4L);
        Character char3 = characterService.getCharacterById(21L);
        user.getActiveCharacters().add(char1);
        user.getActiveCharacters().add(char2);
        user.getActiveCharacters().add(char3);
        boolean joined = room.addPlayer(user);
        if (joined) {
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

    private void broadcastRoomsList() {
        messagingTemplate.convertAndSend("/topic/rooms", getAllRooms());
    }

}