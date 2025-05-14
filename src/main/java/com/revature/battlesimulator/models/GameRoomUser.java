// GameLobbyUser.java
package com.revature.battlesimulator.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GameRoomUser {
    private Long userId;
    private String username;
    private List<Character> activeCharacters;// Array to hold 3 active characters
    private List<Character> defatedCharacters; // Array to hold 3 inactive characters

    // Simple constructor
    public GameRoomUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
        this.activeCharacters = new ArrayList<>();
        this.defatedCharacters = new ArrayList<>();
    }

    // Constructor with active and inactive characters
    public GameRoomUser(Long userId, String username, List<Character> activeCharacters,
            List<Character> defatedCharacters) {
        this.userId = userId;
        this.username = username;
        this.activeCharacters = activeCharacters;
        this.defatedCharacters = defatedCharacters;
    }
}