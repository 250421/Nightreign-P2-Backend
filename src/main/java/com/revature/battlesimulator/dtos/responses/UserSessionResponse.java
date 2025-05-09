package com.revature.battlesimulator.dtos.responses;

import com.revature.battlesimulator.models.Role;
import com.revature.battlesimulator.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {
    private Long id;
    private String username;
    private Role role;

    public UserSessionResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}