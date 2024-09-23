package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.UserLoginBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public Map<String,Object> userAuthentication(UserLoginBean userLoginBean){
        Map<String,Object> tokenMap = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginBean.getUsername(), userLoginBean.getPassword())
            );

            if (authentication.isAuthenticated()) {
                tokenMap.put("token",jwtService.generateToken(userLoginBean.getUsername()));
                Set<String> roles = authentication.getAuthorities().stream()
                        .map(r -> r.getAuthority()).collect(Collectors.toSet());
                tokenMap.put("role",roles);
            }
        }catch (Exception e){

        }
        return tokenMap;

    }
}
