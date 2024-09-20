package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.UserLoginBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public String userAuthentication(UserLoginBean userLoginBean){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginBean.getUsername(), userLoginBean.getPassword())
            );

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(userLoginBean.getUsername());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
