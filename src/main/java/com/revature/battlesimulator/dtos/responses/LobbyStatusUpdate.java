package com.revature.battlesimulator.dtos.responses;

import java.util.List;
import com.revature.battlesimulator.models.GameLobbyUser;
import lombok.Data;

@Data
public class LobbyStatusUpdate {
    private List<GameLobbyUser> onlineUsers;
    private int userCount;
}