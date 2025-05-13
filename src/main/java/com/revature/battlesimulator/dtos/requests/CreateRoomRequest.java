package com.revature.battlesimulator.dtos.requests;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String roomName;
    private Long userId;
    private String username;
}