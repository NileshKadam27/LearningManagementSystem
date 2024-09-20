package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.UserCreationBean;
import com.example.LearningManagementSystem.bean.UserLoginBean;
import com.example.LearningManagementSystem.bean.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface UserService extends UserDetailsService {


    public UserResponse createUser(UserCreationBean userCreationBean) throws Exception;
}
