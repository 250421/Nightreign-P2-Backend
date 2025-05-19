package com.revature.battlesimulator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.revature.battlesimulator.models.Role;
import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.repositories.UserRepository;

@Service
public class UserService {

    //@Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public void registerNewUser(User User) {
        String rawPassword = User.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User.setPassword(encodedPassword);
        User.setRole(Role.USER);
        userRepository.save(User);
    }

    // logs in a user if the username and password combination are valid
    public User login(String username, String rawPassword) {
        User User = userRepository.findByUsernameIgnoreCase(username);
        if (User != null && passwordEncoder.matches(rawPassword, User.getPassword())) {
            return User;
        }
        return null;
    }

    // checks to see if a username is associated with a User or not
    // returns a User object of the User with the username if it exists, null
    // otherwise
    public User findByUsername(String username) {
        if (userRepository.findByUsernameIgnoreCase(username) != null) {
            return userRepository.findByUsernameIgnoreCase(username);
        }
        return null;
    }

    // checks to see if a UserId is associated with a User or not
    // returns a User object of the User with the UserId if it exists, null
    // otherwise
    public User findByUserId(long UserId) {
        if (userRepository.findById(UserId) == null) {
            return null;
        } else {
            return userRepository.findById(UserId);
        }
    }
}