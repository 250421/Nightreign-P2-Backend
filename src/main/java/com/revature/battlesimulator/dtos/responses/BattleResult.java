package com.revature.battlesimulator.dtos.responses;

public class BattleResult {
    private String winner;
    private String reason;

    // Constructors
    public BattleResult() {}
    public BattleResult(String winner, String reason) {
        this.winner = winner;
        this.reason = reason;
    }

    // Getters and Setters
    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}