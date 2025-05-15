package com.revature.battlesimulator.dtos.requests;

import lombok.Data;

@Data
public class IsReadyRequest {
    private String roomId;
    private Long userId;
    private String username;
    private boolean battleReady;
    private Long character_id;
}
