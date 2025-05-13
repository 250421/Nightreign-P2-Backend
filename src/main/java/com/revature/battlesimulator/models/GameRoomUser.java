// GameLobbyUser.java
package com.revature.battlesimulator.models;

import lombok.Data;

@Data
public class GameRoomUser {
    private Long userId;
    private String username;

    // Simple constructor
    public GameRoomUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}