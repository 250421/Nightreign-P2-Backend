package com.project2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2.entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

    User findByUsernameAndPassword(String email, String password);
    User findByUsernameIgnoreCase(String email);
    User findByPassword(String password);
    User findById(int id);
}