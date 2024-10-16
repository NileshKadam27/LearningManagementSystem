package com.example.LearningManagementSystem.bean;


import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Valid
public class UserCreationBean {

    @NotEmpty(message = "username must not be null or empty")
    private String username;

    @NotEmpty(message = "password must not be null or empty")
    private String password;

    @NotEmpty(message = "roleCode must not be null or empty")
    private String roleCode;

    private String firstname;

    private String lastname;

    private String email;

    private String mobile;

    private String experience;

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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
