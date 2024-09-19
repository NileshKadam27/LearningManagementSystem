package com.example.LearningManagementSystem.controller;


import com.example.LearningManagementSystem.bean.ErrorBean;
import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.bean.UserCreationBean;
import com.example.LearningManagementSystem.bean.UserLoginBean;
import com.example.LearningManagementSystem.service.UserService;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("/v1/user/register")
    public ResponseEntity<Object> createUser(@RequestBody UserCreationBean userCreationBean) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(userService.createUser(userCreationBean));
        responseBean.setMessage("User Created.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.CREATED);
    }

    @PostMapping("/v1/user/login")
    public ResponseEntity<Object> login(UserLoginBean userLoginBean, HttpHeaders httpHeaders) throws Exception {
        ResponseBean responseBean = new ResponseBean();

        return ResponseHandler.responseEntity(responseBean, HttpStatus.CREATED);
    }



}
