package com.revature.battlesimulator.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GameRoom {
    private String roomId;
    private GameLobbyUser player1;
    private GameLobbyUser player2;
    private GameStatus status; // WAITING, READY, IN_PROGRESS
    private LocalDateTime createdAt;
}
