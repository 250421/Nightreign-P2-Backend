package com.revature.battlesimulator.dtos.requests;

import lombok.Data;

@Data
public class LeaveRoomRequest {
    private String roomId;
    private Long userId;
}