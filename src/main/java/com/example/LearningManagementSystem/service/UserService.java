package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.UserCreationBean;
import com.example.LearningManagementSystem.bean.UserLoginBean;
import com.example.LearningManagementSystem.bean.UserResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface UserService extends UserDetailsService {


    public UserResponse createUser(UserCreationBean userCreationBean) throws Exception;

    public Map<String,String> userLogin(UserLoginBean userLoginBean, HttpHeaders httpHeaders);
}
