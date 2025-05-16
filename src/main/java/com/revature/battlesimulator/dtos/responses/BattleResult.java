package com.revature.battlesimulator.dtos.responses;

import lombok.Data;

@Data
public class BattleResult {
    private String winner;
    private String reason;
    private String winningCharacter;

}