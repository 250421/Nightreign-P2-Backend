package com.revature.battlesimulator.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.revature.battlesimulator.services.LobbyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LobbyController {
    private final LobbyService lobbyService;

    @MessageMapping("/lobby/join")
    public void joinLobby(Long userId) {
        lobbyService.userJoinedLobby(userId);
    }

    @MessageMapping("/lobby/leave")
    public void leaveLobby(Long userId) {
        lobbyService.userLeftLobby(userId);
    }

}
