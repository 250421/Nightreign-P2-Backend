package com.revature.battlesimulator.services;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.battlesimulator.dtos.responses.BattleResult;
import com.revature.battlesimulator.utils.custom_exceptions.OpenAIException;

@Service
public class BattleService {

    private final OpenAiChatModel chatClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public BattleService(OpenAiChatModel chatClient, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
    }

    public BattleResult simulateBattle(String fighter1, String fighter2) {
        String prompt = String.format("""
            Respond strictly in JSON format.
            Return a JSON object with two fields: "winner" and "reason".
        
            Simulate a battle between:
            - Player 1: %s
            - Player 2: %s
        
            If both players selected the same character, treat them as versions from different timelines or universes with distinct strategies, personalities, or combat experience. Choose a winner based on who would realistically come out on top in a head-to-head battle.
            Label the winner as either "Player 1" or "Player 2" in the "winner" field.
            """, fighter1, fighter2);

        String response = chatClient.call(prompt);

        try {
            if  (fighter1 == null || fighter2 == null) {
                throw new IllegalArgumentException("Fighter names cannot be null");
            }
            return objectMapper.readValue(response, BattleResult.class);
        } catch (Exception e) {
            throw new OpenAIException(e.getMessage(), e);
        }
    }
}