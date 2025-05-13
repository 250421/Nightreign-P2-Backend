package com.revature.battlesimulator.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import com.revature.battlesimulator.dtos.requests.CreateRoomRequest;
import com.revature.battlesimulator.dtos.requests.JoinRoomRequest;
import com.revature.battlesimulator.dtos.requests.LeaveRoomRequest;
import com.revature.battlesimulator.models.GameRoom;
import com.revature.battlesimulator.services.GameRoomService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class GameRoomController {
    private final GameRoomService gameRoomService;

    // WebSocket endpoint for creating a room
    @MessageMapping("/room/create")
    public void createRoom(CreateRoomRequest request) {
        System.out.println("Received room creation request: " + request);
        gameRoomService.createRoom(request.getRoomName(), request.getUserId(), request.getUsername());
        // Updates will be broadcast through the service
    }

    // WebSocket endpoint for joining a room
    @MessageMapping("/room/join")
    public void joinRoom(JoinRoomRequest request) {
        System.out.println("Received join room request: " + request);
        gameRoomService.joinRoom(request.getRoomId(), request.getUserId(), request.getUsername());
    }

    // WebSocket endpoint for leaving a room
    @MessageMapping("/room/leave")
    public void leaveRoom(LeaveRoomRequest request) {
        System.out.println("Received leave room request: " + request);
        gameRoomService.leaveRoom(request.getRoomId(), request.getUserId());
    }

    // Optional REST endpoints if you still need them
    @GetMapping("/rooms")
    @ResponseBody
    public List<GameRoom> getAllRooms() {
        return gameRoomService.getAllRooms();
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<GameRoom> getRoomDetails(@PathVariable String roomId) {
        System.out.println("Getting room details for ID: " + roomId);
        GameRoom room = gameRoomService.getRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }
}