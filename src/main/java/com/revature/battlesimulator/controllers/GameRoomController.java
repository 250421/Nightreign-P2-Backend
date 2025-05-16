package com.revature.battlesimulator.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import com.revature.battlesimulator.dtos.requests.CreateRoomRequest;
import com.revature.battlesimulator.dtos.requests.JoinRoomRequest;
import com.revature.battlesimulator.dtos.requests.LeaveRoomRequest;
import com.revature.battlesimulator.dtos.requests.UpdatePlayerRequest;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.services.GameRoomService;
import com.revature.battlesimulator.utils.custom_exceptions.GameRoomNotFoundException;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class GameRoomController {
    private final GameRoomService gameRoomService;
    private static final Logger logger = LoggerFactory.getLogger(GameRoomController.class);

    /**
     * Handles a request to create a new game room.
     * 
     * @param request The request containing the room name and user ID
     * @return GameRoom - The created game room
     */
    @MessageMapping("/room/create")
    @SendTo("/topic/room/created")
    public GameRoom createRoom(CreateRoomRequest request) {
        logger.info("Received room creation request: {}", request);
        GameRoom room = gameRoomService.createRoom(request.getRoomName(), request.getUserId(), request.getUsername());
        return room;
    }

    /**
     * Handles a request to join a game room.
     * 
     * @param request The request containing the room ID and user ID
     */
    @MessageMapping("/room/join")
    public void joinRoom(JoinRoomRequest request) {
        logger.info("Received join room request: {}", request);
        gameRoomService.joinRoom(request.getRoomId(), request.getUserId(), request.getUsername());
    }

    /**
     * Handles a request to leave a game room.
     * 
     * @param request The request containing the room ID and user ID
     */
    @MessageMapping("/room/leave")
    public void leaveRoom(LeaveRoomRequest request) {
        logger.info("Received leave room request: {}", request);
        gameRoomService.leaveRoom(request.getRoomId(), request.getUserId());
    }

    /**
     * Updates the player's status in the game room.
     * 
     * @param request The request containing the room ID, user ID, active
     *                characters,
     *                and readiness status
     */
    @MessageMapping("/room/ready")
    public void updatePlayer(UpdatePlayerRequest request) {
        logger.info("Received update player request: {}", request);
        gameRoomService.updatePlayer(request.getRoomId(), request.getUserId(),
                request.getActiveCharacters(), request.isReadyForBattle());
    }

    /**
     * Retrieves the list of all game rooms.
     * 
     * @return List<GameRoom> - A list of all game rooms
     */

    @GetMapping("/rooms")
    @ResponseBody
    public List<GameRoom> getAllRooms() {
        return gameRoomService.getAllRooms();
    }

    /**
     * Retrieves the details of a specific game room by its ID.
     * 
     * @param roomId The unique identifier of the game room
     * @return ResponseEntity<GameRoom> - The details of the game room, or 404 if
     *         not found
     */
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<GameRoom> getRoomDetails(@PathVariable String roomId) {
        logger.info("Getting room details for ID: {}", roomId);
        GameRoom room = gameRoomService.getRoomById(roomId);
        if (room == null) {
            throw new GameRoomNotFoundException("Room not found with ID: " + roomId);
        }
        return ResponseEntity.ok(room);
    }
}