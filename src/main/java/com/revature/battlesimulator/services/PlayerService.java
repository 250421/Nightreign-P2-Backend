package com.revature.battlesimulator.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.models.Player;
import com.revature.battlesimulator.repositories.PlayerRepository;

import lombok.Data;

@Service
@Data
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService (PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Character> findCharactersByUserAndCharIsActiveTrue(User user) {
        return playerRepository.findCharactersByUserAndCharIsActiveTrue(user);
    }

    public List<Character> findCharactersByUserAndCharIsActiveFalse(User user) {
        return playerRepository.findCharactersByUserAndCharIsActiveFalse(user);
    }

    public boolean existsByUserAndCharacter(User user, Character c) {
        return playerRepository.existsByUserAndCharacter(user, c);
    }

    public void selectChar(Player p) {
        playerRepository.save(p);
    }

    public void disableChar(int userId, int charId) {
        playerRepository.disableCharacterByUserAndCharacter(userId, charId);
    }

    public void deleteChars(int userid) {
        playerRepository.deleteByUser(userid);
    }
}
