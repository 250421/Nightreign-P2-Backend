package com.revature.battlesimulator.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.battlesimulator.models.User;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    User findByUsernameAndPassword(String username, String password);
    User findByUsernameIgnoreCase(String username);
    User findByPassword(String password);
    User findById(long id);
    List<User> findByRoom_Id(String roomId);
}