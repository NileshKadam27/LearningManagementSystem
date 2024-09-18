package com.example.LearningManagementSystem.utils;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

    public static ResponseEntity<Object> responseEntity(String message, Object payload, HttpStatus httpStatus) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("payload", payload);
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<Object> responseEntity(String message ,HttpStatus httpStatus) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, httpStatus);
    }
}
