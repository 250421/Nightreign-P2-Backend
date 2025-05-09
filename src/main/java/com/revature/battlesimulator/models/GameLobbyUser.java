package com.revature.battlesimulator.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GameLobbyUser {
    private Long userId;
    private String username;
    private UserStatus status; // ONLINE, IN_GAME, etc.
    private LocalDateTime joinedAt;
}
