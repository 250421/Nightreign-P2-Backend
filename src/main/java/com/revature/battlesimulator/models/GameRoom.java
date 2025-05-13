package com.revature.battlesimulator.models;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class GameRoom {
    private String id;
    private String name;
    private GameRoomUser creator;
    private List<GameRoomUser> players = new ArrayList<>();

    public GameRoom(String name, GameRoomUser creator) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        this.name = name;
        this.creator = creator;
        this.players.add(creator); // Creator is also a player
    }

    public boolean addPlayer(GameRoomUser user) {
        // Check if the user is already in the room
        for (GameRoomUser player : players) {
            if (player.getUserId().equals(user.getUserId())) {
                return false; // User already in the room
            }
        }

        players.add(user);
        return true;
    }

    public boolean removePlayer(Long userId) {
        return players.removeIf(player -> player.getUserId().equals(userId));
    }
}