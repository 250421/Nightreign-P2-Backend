package com.revature.battlesimulator.controllers;   

import org.springframework.web.bind.annotation.*;

import com.revature.battlesimulator.services.BattleService;

@RestController
@RequestMapping("/battle")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    //http://localhost:8080/battlesimulator/api/battle?fighter1=Kirby&fighter2=Superman
    @GetMapping
    public String battle(@RequestParam String fighter1, @RequestParam String fighter2) {
        return battleService.simulateBattle(fighter1, fighter2);
    }
}