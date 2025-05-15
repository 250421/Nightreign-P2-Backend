package com.revature.battlesimulator.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.battlesimulator.dtos.responses.BattleResult;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.models.GameRoomUser;
import com.revature.battlesimulator.utils.custom_exceptions.OpenAIException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleService {

    private final OpenAiChatModel chatClient;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private final GameRoomService gameRoomService;

    public boolean isReady(String roomId, Long userId, Long characterId, boolean isReady) {
        // This method is not implemented in the original code.
        // You can implement it based on your requirements.
        GameRoomUser player = gameRoomService.getUserFromRoom(roomId, userId);
        if (player == null) {
            return false;
        }

        player.setBattleReady(isReady);
        player.getActiveCharacters().stream()
                .filter(character -> character.getCharacter_id().equals(characterId))
                .findFirst()
                .ifPresent(player::setSelectedCharacter);

        messagingTemplate.convertAndSend("/topic/battle/isReady/" + roomId,
                gameRoomService.getRoomById(roomId).getPlayers());
        allPlayersReady(roomId);
        return false;
    }

    public void allPlayersReady(String roomId) {

        List<GameRoomUser> players = gameRoomService.getRoomById(roomId).getPlayers();
        boolean allReady = players.stream().allMatch(GameRoomUser::isBattleReady);// For example, send a message to the
                                                                                  // player or log an error
        if (allReady) {
            // Logic to handle when all players are ready
            BattleResult battleResult = simulateBattle(players.get(0),
                    players.get(1));
            messagingTemplate.convertAndSend("/topic/battle/result/" + roomId, battleResult);
            String winner = battleResult.getWinner();
            GameRoomUser loser = winner.equals(players.get(0).getUsername()) ? players.get(1) : players.get(0);
            loser.getDefeatedCharacters().add(loser.getSelectedCharacter());

            players.forEach(player -> player.setBattleReady(false));
            messagingTemplate.convertAndSend("/topic/battle/isReady/" + roomId,
                gameRoomService.getRoomById(roomId).getPlayers());
        }
    }

    public BattleResult simulateBattle(GameRoomUser player1, GameRoomUser player2) {
        String fighter1 = player1.getSelectedCharacter().getName();
        String fighter2 = player2.getSelectedCharacter().getName();
        String player1Name = player1.getUsername();
        String player2Name = player2.getUsername();
        String prompt = String.format(
                """
                        Respond strictly in JSON format.
                        Return a JSON object with two fields: "winner" and "reason".

                        Simulate a battle between:
                        - %s: %s
                        - %s: %s

                        If both players selected the same character, treat them as versions from different timelines or universes with distinct strategies, personalities, or combat experience. Choose a winner based on who would realistically come out on top in a head-to-head battle.
                        Label the winner as either "%s" or "%s" in the "winner" field.
                        """,
                player1Name, fighter1, player2Name, fighter2, player1Name, player2Name);

        String response = chatClient.call(prompt);

        try {
            if (fighter1 == null || fighter2 == null) {
                throw new IllegalArgumentException("Fighter names cannot be null");
            }
            return objectMapper.readValue(response, BattleResult.class);
        } catch (Exception e) {
            throw new OpenAIException(e.getMessage(), e);
        }
    }
}