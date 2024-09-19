package com.example.LearningManagementSystem.bean;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;

    private Long rolekey;

    private String username;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRolekey() {
        return rolekey;
    }

    public void setRolekey(Long rolekey) {
        this.rolekey = rolekey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
