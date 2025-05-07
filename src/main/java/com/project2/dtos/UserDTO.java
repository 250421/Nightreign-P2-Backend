package com.project2.dtos;

public class UserDTO {
    private Integer id;
    private String username;
    private String role;

    public UserDTO () {

    }

    public UserDTO(Integer id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
