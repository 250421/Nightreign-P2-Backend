package com.revature.battlesimulator.repositories;

import com.revature.battlesimulator.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    User findByUsernameAndPassword(String username, String password);
    User findByUsernameIgnoreCase(String username);
    User findByPassword(String password);
    User findById(int id);
}