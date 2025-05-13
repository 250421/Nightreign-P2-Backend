package com.revature.battlesimulator.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.battlesimulator.dtos.responses.ErrorResponse;
import com.revature.battlesimulator.dtos.responses.UserSessionResponse;
import com.revature.battlesimulator.models.User;
import com.revature.battlesimulator.services.SessionService;
import com.revature.battlesimulator.services.UserService;
import com.revature.battlesimulator.utils.custom_exceptions.InsufficientPermissionException;
import com.revature.battlesimulator.utils.custom_exceptions.InvalidInformationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://3.142.213.58:8082/"}, allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody User user, HttpServletRequest request) {
        // Check if the user is already logged in
        if (sessionService.getActiveUserSession() != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }
        // Check if the account already exists
        if (userService.findByUsername(user.getUsername()) != null) {
            ErrorResponse e = new ErrorResponse("Username already in use.");
            return ResponseEntity.status(409).body(e);
        }

        // Validate account fields
        if (user.getUsername().length() < 5) {
            throw new InvalidInformationException("Username must be at least 5 characters long");
        }
        if (user.getPassword().length() < 8) {
            throw new InvalidInformationException("Password must be at least 8 characters long");
        }
        if (!user.getPassword().matches(".*[a-z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one lowercase letter\"");
            return ResponseEntity.status(400).body(e);
            // throw new InvalidInformationException("Password must contain at least one
            // lowercase letter\"");
        }
        if (!user.getPassword().matches(".*[A-Z].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one uppercase letter");
            return ResponseEntity.status(400).body(e);
            // throw new InvalidInformationException("Password must contain at least one
            // uppercase letter");
        }
        if (!user.getPassword().matches(".*\\d.*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one number");
            return ResponseEntity.status(400).body(e);
            // throw new InvalidInformationException("Password must contain at least one
            // number");
        }
        if (!user.getPassword().matches(".*[@$!%*?&].*")) {
            ErrorResponse e = new ErrorResponse("Password must contain at least one special character (@$!%*?&)");
            return ResponseEntity.status(400).body(e);
            // throw new InvalidInformationException("Password must contain at least one
            // special character (@$!%*?&)");
        }

        // Register the new user
        userService.registerNewUser(user);
        return ResponseEntity.status(201)
                .build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
        // Check if the user is already logged in
        UserSessionResponse userSession = sessionService.getActiveUserSession();
        if (userSession != null) {
            ErrorResponse e = new ErrorResponse("You are already logged in.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
            // throw new AuthorizationErrorException("User already logged in.");
        }

        // Attempt login
        User found = userService.login(user.getUsername(), user.getPassword());
        if (found == null) {
            ErrorResponse e = new ErrorResponse("Invalid Credentials");
            return ResponseEntity.status(404).body(e);
            // throw new InvalidInformationException("Invalid Credentials");
        }
        sessionService.startUserSession(found);

        return ResponseEntity.status(HttpStatus.OK).body(found);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        sessionService.endUserSession();
        ErrorResponse e = new ErrorResponse("Successfully logged out");
        return ResponseEntity.status(200).body(e);
    }

    @GetMapping
    public ResponseEntity<UserSessionResponse> getSession() {
        UserSessionResponse userSession = sessionService.getActiveUserSession();
        if (userSession == null) {
            throw new InsufficientPermissionException("No active session found");
        }
        return ResponseEntity.ok(userSession);
    }

}
