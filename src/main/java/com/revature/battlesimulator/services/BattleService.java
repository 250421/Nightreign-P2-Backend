package com.revature.battlesimulator.services;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BattleService {

    private final OpenAiChatModel chatClient;

    @Autowired
    public BattleService(OpenAiChatModel chatClient) {
        this.chatClient = chatClient;
    }

    public String simulateBattle(String fighter1, String fighter2) {
        String prompt = String.format("Return a JSON object with a winner and reason for who would win in a fight between %s and %s.", fighter1, fighter2);
        return chatClient.call(prompt);
    }
}