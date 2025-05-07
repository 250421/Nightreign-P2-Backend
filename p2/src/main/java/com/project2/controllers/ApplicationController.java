package com.project2.controllers;

import com.project2.ErrorResponse;
import com.project2.entities.User;
import com.project2.entities.UserDTO;
import com.project2.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class ApplicationController {

    private UserService userService;

    @Autowired
    public ApplicationController(UserService accountService) {
        this.userService = accountService;
    }

    @PostMapping("/auth/sign-up")
    public ResponseEntity<?> register(@RequestBody User user, HttpServletRequest request) {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }

        // Check if the account already exists
        if (userService.findByUsername(user.getUsername()) != null) {
            ErrorResponse e = new ErrorResponse("Username already exists");
            return ResponseEntity.status(409).body(e);
        }

        // Validate account fields
        if (user.getUsername().length() < 5) {
            ErrorResponse e = new ErrorResponse("Username must be at least 5 characters long");
            return ResponseEntity.status(400).body(e);
        }
        if (user.getPassword().length() < 8) {
            ErrorResponse e = new ErrorResponse("Password must be at least 8 characters long");
            return ResponseEntity.status(400).body(e);
        }
        if (!user.getPassword().matches(".*[a-z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one lowercase letter\"");
            return ResponseEntity.status(400).body(e);
        }
        if (!user.getPassword().matches(".*[A-Z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one uppercase letter");
            return ResponseEntity.status(400).body(e);
        }
        if (!user.getPassword().matches(".*\\d.*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one number");
            return ResponseEntity.status(400).body(e);
        }
        if (!user.getPassword().matches(".*[@$!%*?&].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one special character (@$!%*?&)");
            return ResponseEntity.status(400).body(e);
        }

        // Register the new user
        userService.registerNewUser(user);
        UserDTO accountDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.status(201)
                .body(accountDTO);
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }

        // Attempt login
        User found = userService.login(user.getUsername(), user.getPassword());
        if (found == null) {
            ErrorResponse e = new ErrorResponse("Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
        }

        // Create a session and store user information
        session = request.getSession(true);
        UserDTO dto = new UserDTO(found.getId(), found.getUsername(), found.getRole());
        session.setAttribute("user", dto);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/auth")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Session is null");
            ErrorResponse e = new ErrorResponse("Access denied: User not logged in.");
            return ResponseEntity.status(401).body(e);
        }

        Object user = session.getAttribute("user");
        if (user == null) {
            ErrorResponse e = new ErrorResponse("Access denied: User not logged in.");
            return ResponseEntity.status(401).body(e);
        }

        UserDTO accountDTO = (UserDTO) user;
        return ResponseEntity.ok(accountDTO);
    }
}
