package com.example.LearningManagementSystem.bean;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;


@Validated
public class UserLoginBean {

    @NotEmpty(message = "username must not be null or empty")
    private String username;

    @NotEmpty(message = "password must not be null or empty")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
