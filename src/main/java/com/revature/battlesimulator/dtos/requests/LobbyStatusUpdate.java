package com.revature.battlesimulator.dtos.requests;

import java.util.List;

import com.revature.battlesimulator.models.GameLobbyUser;
import com.revature.battlesimulator.models.GameRoom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyStatusUpdate {
    private List<GameLobbyUser> onlineUsers;
    private List<GameRoom> gameRooms;

    public int getAvailableRoomCount() {
        if (gameRooms == null)
            return 0;
        return (int) gameRooms.stream()
                .filter(room -> room.getPlayer2() == null)
                .count();
    }

    public int getTotalUserCount() {
        return onlineUsers == null ? 0 : onlineUsers.size();
    }
}
