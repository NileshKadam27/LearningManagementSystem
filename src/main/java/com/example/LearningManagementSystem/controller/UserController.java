package com.example.LearningManagementSystem.controller;


import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.bean.UserCreationBean;
import com.example.LearningManagementSystem.bean.UserLoginBean;
import com.example.LearningManagementSystem.service.AuthenticationService;
import com.example.LearningManagementSystem.service.UserService;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@Validated
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;



    @PostMapping("/v1/user/register")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreationBean userCreationBean,  BindingResult result) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(userService.createUser(userCreationBean));
        responseBean.setMessage("User Created.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.CREATED);
    }


    @PostMapping("/v1/user/login")
    public ResponseEntity<Object> login(@Valid @RequestBody UserLoginBean userLoginBean) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        Map<String,Object> tokenMap =authenticationService.userAuthentication(userLoginBean);
        responseBean.setPayload(tokenMap);
        responseBean.setMessage("SUCCESS");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.CREATED);
    }



}
