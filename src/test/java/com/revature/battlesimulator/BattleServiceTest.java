package com.revature.battlesimulator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.battlesimulator.dtos.responses.BattleResult;
import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.models.GameRoomUser;
import com.revature.battlesimulator.services.BattleService;
import com.revature.battlesimulator.services.GameRoomService;
import com.revature.battlesimulator.utils.custom_exceptions.OpenAIException;

class BattleServiceTest {

    @Mock
    private OpenAiChatModel chatClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GameRoomService gameRoomService;

    @InjectMocks
    private BattleService battleService;

    private GameRoomUser user1;
    private GameRoomUser user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Character character1 = new Character();
        character1.setCharacter_id(1L);
        character1.setName("C1");

        Character character2 = new Character();
        character2.setCharacter_id(2L);
        character2.setName("C2");

        user1 = new GameRoomUser(111L, "Username1");
        user1.setActiveCharacters(new ArrayList<>(List.of(character1)));

        user2 = new GameRoomUser(222L, "Username2");
        user2.setActiveCharacters(new ArrayList<>(List.of(character2)));

        GameRoom room = new GameRoom("room1", user1);
        room.setPlayers(new ArrayList<>(List.of(user1, user2)));

        when(gameRoomService.getRoomById("room1")).thenReturn(room);
    }

    @Test
    void testIsReadySuccess() {
        when(gameRoomService.getUserFromRoom("room1", 111L)).thenReturn(user1);

        boolean result = battleService.isReady("room1", 111L, 1L, true);

        assertTrue(result);
        assertTrue(user1.isBattleReady());
        assertEquals("C1", user1.getSelectedCharacter().getName());

        verify(messagingTemplate).convertAndSend(eq("/topic/battle/isReady/room1"), anyList());
    }

    @Test
    void testIsReadyUserNotFound() {
        when(gameRoomService.getUserFromRoom("room1", 909L)).thenReturn(null);
        boolean result = battleService.isReady("room123", 909L, 1L, true);
        assertFalse(result);
    }

    @Test
    void testAllPlayersReadyStartsBattle() throws Exception {
        user1.setBattleReady(true);
        user1.setSelectedCharacter(user1.getActiveCharacters().get(0));
        user2.setBattleReady(true);
        user2.setSelectedCharacter(user2.getActiveCharacters().get(0));

        BattleResult result = new BattleResult("Username1", "Better skillset", "C1");

        when(chatClient.call(anyString())).thenReturn("{\"winner\":\"Username1\",\"reason\":\"Better skillset\",\"winningCharacter\":\"C1\"}");
        when(objectMapper.readValue(anyString(), eq(BattleResult.class))).thenReturn(result);

        battleService.allPlayersReady("room1");

        assertEquals(1, user2.getDefeatedCharacters().size());
        assertFalse(user1.isBattleReady());
        assertFalse(user2.isBattleReady());

        verify(messagingTemplate).convertAndSend(eq("/topic/battle/result/room1"), eq(result));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/battle/isReady/room1"), anyList());
    }

    @Test
    void testSimulateBattleThrowsOpenAIErrorOnNullCharacterNames() {
        user1.setSelectedCharacter(new Character(1L, null, "DC", "imgURL"));
        user2.setSelectedCharacter(new Character(2L, null, "Marvel", "imgUrl"));
        System.out.println("User 1 char: "+ user1.getSelectedCharacter().getName());
        Exception exception = assertThrows(OpenAIException.class, () ->
            battleService.simulateBattle(user1, user2)
        );

        assertEquals("Error with OpenAI: Fighter names cannot be null", exception.getMessage());
    }

    @Test
    void testSimulateBattleSuccess() throws Exception {
        GameRoomUser user1 = new GameRoomUser(121L, "Username1");
        user1.setSelectedCharacter(new Character(1L, "C1", "Marvel", "imgUrl"));

        GameRoomUser user2 = new GameRoomUser(212L, "Username2");
        user2.setSelectedCharacter(new Character(2L, "C2", "DC", "imgUrl"));
        
        String fakeResponse = "{\"winner\":\"Username1\",\"reason\":\"Outsmarted opponent\",\"winningCharacter\":\"C1\"}";

        BattleResult expectedResult = new BattleResult("Username1", "Outsmarted opponent", "C1");

        when(chatClient.call(anyString())).thenReturn(fakeResponse);
        when(objectMapper.readValue(fakeResponse, BattleResult.class)).thenReturn(expectedResult);
        BattleResult actualResult = battleService.simulateBattle(user1, user2);

        assertEquals("Username1", actualResult.getWinner());
        assertEquals("Outsmarted opponent", actualResult.getReason());
        assertEquals("C1", actualResult.getWinningCharacter());

        verify(chatClient).call(anyString());
        verify(objectMapper).readValue(fakeResponse, BattleResult.class);
    }
}
