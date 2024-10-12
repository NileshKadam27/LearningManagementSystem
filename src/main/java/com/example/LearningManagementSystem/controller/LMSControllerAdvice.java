package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.bean.ErrorBean;
import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.exception.CourseAlreadyExist;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.exception.LmsException;
import com.example.LearningManagementSystem.exception.UserNameAlreadyExist;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

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


    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<?> handleValidationExceptions(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ErrorBean errorBean = new ErrorBean();
        ex.getConstraintViolations().forEach(error -> {
            errors.add(error.getMessage());
        });
        if(!errors.isEmpty()) {
            errorBean.setErrorMessage(errors.get(0));
            errorBean.setErrorCode("ERROR_001");
        }
        ResponseBean responseBean = new ResponseBean();
        responseBean.setErrorBean(errorBean);
        return new ResponseEntity(responseBean, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<?> handleAuthenticationException(Exception ex) {
        ResponseBean responseBean = new ResponseBean();
        ErrorBean errorBean = new ErrorBean();
        errorBean.setErrorCode("AUTH-001");
        errorBean.setErrorMessage("Authentication failed");
        responseBean.setErrorBean(errorBean);
        return new ResponseEntity(responseBean, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CourseAlreadyExist.class)
    public ResponseEntity<Object> handleCourseAlreadyExistException(Exception e){
        ErrorBean errorBean = new ErrorBean();
        errorBean.setErrorCode("LMS_010");
        errorBean.setErrorMessage(e.getMessage());
        ResponseBean responseBean = new ResponseBean();
        responseBean.setErrorBean(errorBean);
        return ResponseHandler.responseEntity(responseBean, HttpStatus.NOT_ACCEPTABLE);
    }
}
