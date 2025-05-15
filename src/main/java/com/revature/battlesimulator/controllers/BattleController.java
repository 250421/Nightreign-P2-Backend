package com.revature.battlesimulator.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import com.revature.battlesimulator.dtos.requests.CreateRoomRequest;
import com.revature.battlesimulator.dtos.requests.IsReadyRequest;
import com.revature.battlesimulator.dtos.requests.JoinRoomRequest;
import com.revature.battlesimulator.dtos.responses.BattleResult;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.services.BattleService;


@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @MessageMapping("/battle/isReady")
    public void isReady(IsReadyRequest request) {
        battleService.isReady(request.getRoomId(), request.getUserId(), request.getCharacter_id(), request.isBattleReady());
    }
    

    // http://localhost:8080/battlesimulator/api/battle?fighter1=Kirby&fighter2=Superman
    // @GetMapping
    // public ResponseEntity<BattleResult> battle(@RequestParam String fighter1, @RequestParam String fighter2) {
    //     BattleResult br =  battleService.simulateBattle(fighter1, fighter2);
    //     return ResponseEntity.status(HttpStatus.OK).body(br);
    // }

}