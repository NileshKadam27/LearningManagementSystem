package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.bean.ErrorBean;
import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.exception.LmsException;
import com.example.LearningManagementSystem.exception.UserNameAlreadyExist;
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

    @ExceptionHandler(UserNameAlreadyExist.class)
    public ResponseEntity<Object> UserNameAlreadyExist(Exception e){
        ErrorBean errorBean = new ErrorBean();
        errorBean.setErrorCode("LMS_001");
        errorBean.setErrorMessage(e.getMessage());
        ResponseBean responseBean = new ResponseBean();
        responseBean.setErrorBean(errorBean);
        return ResponseHandler.responseEntity(responseBean, HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(LmsException.class)
    public ResponseEntity<Object> LmsException(LmsException e){
        ErrorBean errorBean = new ErrorBean();
        errorBean.setErrorCode(e.getErrorCode());
        errorBean.setErrorMessage(e.getMessage());
        ResponseBean responseBean = new ResponseBean();
        responseBean.setErrorBean(errorBean);
        return ResponseHandler.responseEntity(responseBean, e.getHttpStatus());
    }
}
