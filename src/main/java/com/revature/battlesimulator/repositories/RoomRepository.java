package com.revature.battlesimulator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.battlesimulator.models.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {    
    
}
