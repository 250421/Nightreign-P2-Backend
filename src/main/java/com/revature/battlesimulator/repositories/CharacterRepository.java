package com.revature.battlesimulator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revature.battlesimulator.models.Character;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    boolean existsByName(String name);

    Character findByName(String name);
}
