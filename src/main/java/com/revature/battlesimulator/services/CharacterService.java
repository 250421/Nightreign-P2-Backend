package com.revature.battlesimulator.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.revature.battlesimulator.dtos.requests.NewCharacterRequest;
import com.revature.battlesimulator.dtos.requests.UpdateCharacterRequest;
import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.repositories.CharacterRepository;
import com.revature.battlesimulator.utils.custom_exceptions.CharacterNotFoundException;
import com.revature.battlesimulator.utils.custom_exceptions.DuplicateCharacterNameException;

import lombok.Data;

@Service
@Data
public class CharacterService {
    private final CharacterRepository characterRepository;

    public List<Character> getAllCharacters() {
        return characterRepository.findAll();
    }

    public Character getCharacterById(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException("Character not found with id: " + id));
    }

    public Character createCharacter(NewCharacterRequest newCharacterRequest) {
        if (isUniqueName(newCharacterRequest.getName())) {
            throw new DuplicateCharacterNameException(
                    "Character name already exists: " + newCharacterRequest.getName());
        }
        Character character = new Character();
        character.setName(newCharacterRequest.getName());
        character.setOrigin(newCharacterRequest.getOrigin());
        return characterRepository.save(character);
    }

    public Character updateCharacter(Long id, UpdateCharacterRequest updateCharacterRequest) {
        Character character = getCharacterById(id);
        if (isUniqueName(updateCharacterRequest.getName())) {
            throw new DuplicateCharacterNameException(
                    "Character name already exists: " + updateCharacterRequest.getName());
        }
        character.setName(updateCharacterRequest.getName());
        character.setOrigin(updateCharacterRequest.getOrigin());
        return characterRepository.save(character);
    }

    public void deleteCharacter(Long id) {
        if (!characterRepository.existsById(id)) {
            throw new CharacterNotFoundException("Character not found with id: " + id);
        }
        Character character = getCharacterById(id);
        characterRepository.delete(character);
    }

    private boolean isUniqueName(String name) {
        return characterRepository.existsByName(name);
    }
}
