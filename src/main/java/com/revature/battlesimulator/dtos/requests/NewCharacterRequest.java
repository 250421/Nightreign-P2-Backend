package com.revature.battlesimulator.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCharacterRequest {
    private String name;
    private String origin;
}
