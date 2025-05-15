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
    private RoomStatus status = RoomStatus.WAITING_FOR_PLAYERS;

    public enum RoomStatus {
        WAITING_FOR_PLAYERS,
        CHOOSING_CHARACTERS,
        IN_BATTLE,
    }

    public GameRoom(String name, GameRoomUser creator) {
        this.id = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        this.name = name;
        this.creator = creator;
        this.players.add(creator);
    }

    public boolean addPlayer(GameRoomUser user) {
        for (GameRoomUser player : players) {
            if (player.getUserId().equals(user.getUserId())) {
                return false;
            }
        }

        players.add(user);
        return true;
    }

    public boolean removePlayer(Long userId) {
        boolean isRemoved = players.removeIf(player -> player.getUserId().equals(userId));

        return isRemoved;
    }
}