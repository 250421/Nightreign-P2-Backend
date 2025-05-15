package com.revature.battlesimulator.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import com.revature.battlesimulator.dtos.requests.IsReadyRequest;
import com.revature.battlesimulator.services.BattleService;


@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @MessageMapping("/battle/isReady")
    public void isReady(IsReadyRequest request) {
        battleService.isReady(request.getRoomId(), request.getUserId(), request.getCharacter_id(), request.isBattleReady());
    }

}