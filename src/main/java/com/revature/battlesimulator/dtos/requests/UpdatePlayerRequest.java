package com.revature.battlesimulator.dtos.requests;

import java.util.List;

import com.revature.battlesimulator.models.Character;

import lombok.Data;

@Data
public class UpdatePlayerRequest {
    private String roomId;
    private Long userId;
    private List<Character> activeCharacters;
    private boolean isReadyForBattle;
}
