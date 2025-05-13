package com.revature.battlesimulator.dtos.requests;

import lombok.Data;

@Data
public class JoinRoomRequest {
    private String roomId;
    private Long userId;
    private String username;
}