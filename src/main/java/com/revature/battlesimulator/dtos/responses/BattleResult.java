package com.revature.battlesimulator.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BattleResult {
    private String winner;
    private String reason;
    private String winningCharacter;

}