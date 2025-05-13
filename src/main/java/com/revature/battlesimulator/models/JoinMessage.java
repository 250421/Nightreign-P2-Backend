package com.revature.battlesimulator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinMessage {
    private String roomId;
    private String username;

    // Constructors, getters, setters
}