package com.revature.battlesimulator.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.battlesimulator.dtos.responses.BattleResult;
import com.revature.battlesimulator.services.BattleService;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/battle")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    // http://localhost:8080/battlesimulator/api/battle?fighter1=Kirby&fighter2=Superman
    @GetMapping
    public ResponseEntity<BattleResult> battle(@RequestParam String fighter1, @RequestParam String fighter2) {
        BattleResult br =  battleService.simulateBattle(fighter1, fighter2);
        return ResponseEntity.status(HttpStatus.OK).body(br);
    }
}