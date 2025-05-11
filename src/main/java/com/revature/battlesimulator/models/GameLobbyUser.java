// GameLobbyUser.java
package com.revature.battlesimulator.models;

import lombok.Data;

@Data
public class GameLobbyUser {
    private Long userId;
    private String username;

    // Simple constructor
    public GameLobbyUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}