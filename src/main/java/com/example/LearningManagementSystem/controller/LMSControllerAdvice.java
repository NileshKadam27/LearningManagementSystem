package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LMSControllerAdvice {

    @ExceptionHandler(EntityDataNotFound.class)
    public ResponseEntity<Object> entityDataNotFound(Exception e){
        return ResponseHandler.responseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
