// GameLobbyUser.java
package com.revature.battlesimulator.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GameRoomUser {
    private Long userId;
    private String username;
    private List<Character> activeCharacters;
    private List<Character> defatedCharacters;

    public GameRoomUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
        this.activeCharacters = new ArrayList<>();
        this.defatedCharacters = new ArrayList<>();
    }

    public GameRoomUser(Long userId, String username, List<Character> activeCharacters,
            List<Character> defatedCharacters) {
        this.userId = userId;
        this.username = username;
        this.activeCharacters = activeCharacters;
        this.defatedCharacters = defatedCharacters;
    }
}