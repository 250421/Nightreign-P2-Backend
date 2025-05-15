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
    private List<Character> defeatedCharacters;
    private boolean battleReady = false;
    private boolean isReadyForBattle = false;
    private Character selectedCharacter = null;

    public GameRoomUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
        this.activeCharacters = new ArrayList<>();
        this.defeatedCharacters = new ArrayList<>();
    }

    public GameRoomUser(Long userId, String username, List<Character> activeCharacters,
            List<Character> defatedCharacters) {
        this.userId = userId;
        this.username = username;
        this.activeCharacters = activeCharacters;
        this.defeatedCharacters = defatedCharacters;
    }
}