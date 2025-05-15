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
import com.revature.battlesimulator.dtos.requests.IsReadyRequest;
import com.revature.battlesimulator.dtos.requests.JoinRoomRequest;
import com.revature.battlesimulator.dtos.requests.LeaveRoomRequest;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.services.BattleService;
import com.revature.battlesimulator.services.GameRoomService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class GameRoomController {
    private final GameRoomService gameRoomService;
    private final BattleService battleService;
    private static final Logger logger = LoggerFactory.getLogger(GameRoomController.class);

    @MessageMapping("/room/create")
    @SendTo("/topic/room/created")
    public GameRoom createRoom(CreateRoomRequest request) {
        logger.info("Received room creation request: {}", request);
        GameRoom room = gameRoomService.createRoom(request.getRoomName(), request.getUserId(), request.getUsername());
        return room;
    }

    @MessageMapping("/room/join")
    public void joinRoom(JoinRoomRequest request) {
        logger.info("Received join room request: {}", request);
        gameRoomService.joinRoom(request.getRoomId(), request.getUserId(), request.getUsername());
    }

    @MessageMapping("/room/leave")
    public void leaveRoom(LeaveRoomRequest request) {
        logger.info("Received leave room request: {}", request);
        gameRoomService.leaveRoom(request.getRoomId(), request.getUserId());
    }

    // @MessageMapping("/battle/isReady")
    // public void isReady(IsReadyRequest request) {
    //     battleService.updatePlayer(request.getRoomId());
    // }

    @GetMapping("/rooms")
    @ResponseBody
    public List<GameRoom> getAllRooms() {
        return gameRoomService.getAllRooms();
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<GameRoom> getRoomDetails(@PathVariable String roomId) {
        logger.info("Getting room details for ID: ", roomId);
        GameRoom room = gameRoomService.getRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }
}