package com.revature.battlesimulator.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revature.battlesimulator.models.Player;
import com.revature.battlesimulator.models.User;

import jakarta.transaction.Transactional;

import com.revature.battlesimulator.models.Character;



public interface PlayerRepository extends JpaRepository<Player, Integer>{
    @Query("SELECT p.character FROM Player p WHERE p.user = :user AND p.charIsActive = true")
    List<Character> findCharactersByUserAndCharIsActiveTrue(@Param("user") User user);  

    @Query("SELECT p.character FROM Player p WHERE p.user = :user AND p.charIsActive = false")
    List<Character> findCharactersByUserAndCharIsActiveFalse(@Param("user") User user);  

    boolean existsByUserAndCharacter(User user, Character c);

    @Modifying
    @Transactional
    @Query("update Player set charIsActive = false where user.id =:userid AND character.id = :charId")
    void disableCharacterByUserAndCharacter(int userid, int charId);

    @Modifying
    @Transactional
    @Query("delete from Player where user.id = :userid")
    void deleteByUser(int userid);

}
