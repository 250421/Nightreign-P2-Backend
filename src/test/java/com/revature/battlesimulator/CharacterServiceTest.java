package com.revature.battlesimulator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.revature.battlesimulator.dtos.requests.NewCharacterRequest;
import com.revature.battlesimulator.dtos.requests.UpdateCharacterRequest;
import com.revature.battlesimulator.models.Character;
import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.repositories.CharacterRepository;
import com.revature.battlesimulator.services.CharacterService;
import com.revature.battlesimulator.utils.custom_exceptions.CharacterNotFoundException;
import com.revature.battlesimulator.utils.custom_exceptions.DuplicateCharacterNameException;
import com.revature.battlesimulator.utils.custom_exceptions.OpenAIException;

public class CharacterServiceTest {
    
    @Mock
    private CharacterRepository characterRepository;

    @InjectMocks
    private CharacterService characterService;

    private Character character;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        character = new Character(1L, "Batman" , "DC", "testUrl");
    }

    @Test
    void addCharacter_NewChar_Saves() {
        when(characterRepository.existsByName("Batman")).thenReturn(false);
        NewCharacterRequest n = new NewCharacterRequest("Batman", "DC", "testUrl");
        characterService.createCharacter(n);
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    void addCharacter_ExistingChar_DoesNotSave() {
        when(characterRepository.existsByName("Batman")).thenReturn(true);
        NewCharacterRequest n = new NewCharacterRequest("Batman", "DC", "testUrl");
        assertThrows(DuplicateCharacterNameException.class, () -> characterService.createCharacter(n));
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void getAllCharacters_ReturnsAll() {
        List<Character> characters = List.of(character);
        when(characterRepository.findAll()).thenReturn(characters);
        List<Character> result = characterService.getAllCharacters();
        assertEquals(1, result.size());
    }

    @Test
    void getAllCharacters_NoChars(){
        when(characterRepository.findAll()).thenReturn(Collections.emptyList());
        List<Character> result = characterService.getAllCharacters();
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_ReturnsCharacter() {
        when(characterRepository.findById(1L)).thenReturn(Optional.of(character));
        Character result = characterService.getCharacterById(1L);
        assertEquals(1L, result.getCharacter_id());
    }

    @Test 
    void findByNonExistantId_DoesNotReturnItem() {
        character.setCharacter_id(123L);
        when(characterRepository.findById(1L)).thenReturn(Optional.of(character));
        Character result = characterService.getCharacterById(1L);
        assertNotEquals(1L, result.getCharacter_id());
    }

    @Test
    void deleteCharacter_DeletesCharacter() {
        when(characterRepository.existsById(1L)).thenReturn(true);
        when(characterRepository.findById(1L)).thenReturn(Optional.of(character));
        characterService.deleteCharacter(1L);
        verify(characterRepository).delete(character);
    }

    @Test
    void deleteCharacter_CharacterDoesntExist() {
        when(characterRepository.existsById(1L)).thenReturn(true);
        when(characterRepository.findById(1L)).thenReturn(Optional.of(character));
        Exception exception = assertThrows(CharacterNotFoundException.class, () ->
            characterService.deleteCharacter(2L)
        );
         assertEquals("Character not found with id: " + 2L, exception.getMessage());
    }

    @Test
    void updateCharacter_UpdatesAndSaves() {
        when(characterRepository.findById(1L)).thenReturn(Optional.of(character));

        Character updated = new Character();
        updated.setName("Superman");
        updated.setOrigin("DC");
        updated.setCharacterImageUrl("newTest");

        UpdateCharacterRequest u = new UpdateCharacterRequest();
        u.setName("Superman");
        u.setOrigin("DC");
        characterService.updateCharacter(1L, u);

        assertEquals("Superman", updated.getName());
        assertEquals("DC", updated.getOrigin());

        verify(characterRepository).save(character);
    }

    @Test
    void updateItem_NewNameAlreadyExists() {
        Character existingCharacter = new Character();
        existingCharacter.setCharacter_id(222L);
        existingCharacter.setName("Superman");

        Character currentCharacter = new Character();
        currentCharacter.setCharacter_id(1L);
        currentCharacter.setName("Spiderman");
        currentCharacter.setOrigin("Marvel");
        currentCharacter.setCharacterImageUrl("Spider");

        when(characterRepository.findById(1L)).thenReturn(Optional.of(currentCharacter));
        when(characterRepository.findByName("Superman")).thenReturn(existingCharacter);

        Character updated = new Character();
        updated.setName("Superman");
        updated.setOrigin("DC");
        updated.setCharacterImageUrl("Super");

        assertEquals("Spiderman", currentCharacter.getName());
        assertEquals("Marvel", currentCharacter.getOrigin());
        assertEquals("Spider", currentCharacter.getCharacterImageUrl());

        verify(characterRepository, never()).save(any());
    }

}