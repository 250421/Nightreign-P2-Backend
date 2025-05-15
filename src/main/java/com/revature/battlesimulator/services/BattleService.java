package com.revature.battlesimulator.services;

import java.util.List;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.battlesimulator.dtos.responses.BattleResult;
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

        //check if user is in the room
        GameRoomUser player = gameRoomService.getUserFromRoom(roomId, userId);
        if (player == null) {
            return false;
        }

        //Set Player player is ready and set selected character
        player.setBattleReady(isReady);
        player.getActiveCharacters().stream()
                .filter(character -> character.getCharacter_id().equals(characterId))
                .findFirst()
                .ifPresent(player::setSelectedCharacter);

        // Send message to all players in the room, the update player info
        messagingTemplate.convertAndSend("/topic/battle/isReady/" + roomId,
                gameRoomService.getRoomById(roomId).getPlayers());

        // Check if all players are ready and start the battle if they are
        allPlayersReady(roomId);
        return true;
    }

    public void allPlayersReady(String roomId) {

        //check if all players are ready
        List<GameRoomUser> players = gameRoomService.getRoomById(roomId).getPlayers();
        boolean allReady = players.stream().allMatch(GameRoomUser::isBattleReady);
        if (allReady) {
            // Simulate the battle and send the result to all players
            BattleResult battleResult = simulateBattle(players.get(0),
                    players.get(1));
            messagingTemplate.convertAndSend("/topic/battle/result/" + roomId, battleResult);
            
            //Logic to check if the winner is the first player or the second player Then reset battle is ready to false
            String winner = battleResult.getWinner();
            GameRoomUser loser = winner.equals(players.get(0).getUsername()) ? players.get(1) : players.get(0);
            loser.getDefeatedCharacters().add(loser.getSelectedCharacter());

            players.forEach(player -> player.setBattleReady(false));
            messagingTemplate.convertAndSend("/topic/battle/isReady/" + roomId,
                gameRoomService.getRoomById(roomId).getPlayers());
        }
    }

    // Simulate a battle between two players using OpenAI's chat model
    // The method takes two GameRoomUser objects as input and returns a BattleResult object
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