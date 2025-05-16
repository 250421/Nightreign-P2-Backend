package com.revature.battlesimulator;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.models.GameRoomUser;
import com.revature.battlesimulator.services.GameRoomService;

class GameRoomServiceTest {

    private GameRoomService gameRoomService;
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        gameRoomService = new GameRoomService(messagingTemplate);
    }

    @Test
    void testCreateRoom() {
        GameRoom room = gameRoomService.createRoom("Room1", 111L, "testUsername");

        assertNotNull(room);
        assertEquals("Room1", room.getName());
        assertEquals(1, room.getPlayers().size());
        assertEquals("testUsername", room.getPlayers().get(0).getUsername());

        verify(messagingTemplate).convertAndSend(eq("/topic/rooms"), eq(List.of(room)));
    }

    @Test
    void testJoinRoomSuccess() {
        GameRoom room = gameRoomService.createRoom("Room1", 11L, "testUsername");

        boolean joined = gameRoomService.joinRoom(room.getId(), 22L, "Username2");

        assertTrue(joined);
        assertEquals(2, room.getPlayers().size());
        assertEquals("testUsername", room.getPlayers().get(0).getUsername());
        assertEquals("Username2", room.getPlayers().get(1).getUsername());

        verify(messagingTemplate, atLeastOnce()).convertAndSend(contains("/topic/room/"), eq(room));
    }

    @Test
    void testJoinRoomFailure_invalidId() {
        boolean joined = gameRoomService.joinRoom("invalidRoom", 212L, "testUsername");

        assertFalse(joined);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void testLeaveRoomSuccess() {
        GameRoom room = gameRoomService.createRoom("Room1", 1L, "Username1");
        gameRoomService.joinRoom(room.getId(), 2L, "Username2");

        boolean left = gameRoomService.leaveRoom(room.getId(), 1L);

        assertTrue(left);
        assertEquals(1, room.getPlayers().size());
        verify(messagingTemplate, atLeastOnce()).convertAndSend(contains("/topic/room/"), eq(room));
    }

    @Test
    void testLeaveRoomDeletesEmptyRoom() {
        GameRoom room = gameRoomService.createRoom("Room1", 123L, "testUsername");

        boolean left = gameRoomService.leaveRoom(room.getId(), 123L);

        assertTrue(left);
        assertNull(gameRoomService.getRoomById(room.getId()));
        verify(messagingTemplate).convertAndSend(eq("/topic/rooms"), eq(List.of(room)));
    }

    @Test
    void testGetUserFromRoom() {
        GameRoom room = gameRoomService.createRoom("Room1", 89L, "TestUsername");
        GameRoomUser user = gameRoomService.getUserFromRoom(room.getId(), 89L);
        assertNotNull(user);
        assertEquals("TestUsername", user.getUsername());
    }

    @Test
    void testGetUserFromRoom_UserIsNull() {
        GameRoom room = gameRoomService.createRoom("Room1", 89L, "TestUsername");
        GameRoomUser user = gameRoomService.getUserFromRoom(room.getId(), 899L);
        assertNull(user);
    }

    @Test
    void testUpdatePlayer() {
        GameRoom room = gameRoomService.createRoom("Room1", 987L, "TestUsername");
        List<Character> characters = Collections.singletonList(new Character());
        gameRoomService.updatePlayer(room.getId(), 987L, characters, true);
        GameRoomUser user = room.getPlayers().get(0);
        assertEquals(characters, user.getActiveCharacters());
        assertTrue(user.isReadyForBattle());
        verify(messagingTemplate, atLeastOnce()).convertAndSend(contains("/topic/room/"), eq(room));
    }

    @Test
    void testUpdateRoomStatusToInBattle() {
        GameRoom room = gameRoomService.createRoom("Room1", 46L, "TestUsername");
        gameRoomService.joinRoom(room.getId(), 48L, "Username2");

        room.getPlayers().get(0).setReadyForBattle(true);
        room.getPlayers().get(1).setReadyForBattle(true);

        gameRoomService.updateRoomStatus(room.getId());

        assertEquals(GameRoom.RoomStatus.IN_BATTLE, room.getStatus());
        verify(messagingTemplate, atLeastOnce()).convertAndSend(contains("/topic/room/"), eq(room));
    }

    @Test
    void testGetAllRooms() {
        gameRoomService.createRoom("Room1", 11L, "Username1");
        gameRoomService.createRoom("Room2", 22L, "Username2");
        gameRoomService.createRoom("Room3", 33L, "Username3");

        List<GameRoom> allRooms = gameRoomService.getAllRooms();

        assertEquals(3, allRooms.size());
    }
}
