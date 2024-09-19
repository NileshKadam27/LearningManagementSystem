package com.example.LearningManagementSystem.exception;

import org.springframework.http.HttpStatus;

public class LmsException extends Exception{

    String errorCode;

    HttpStatus httpStatus;
    public LmsException(String message) {
        super(message);
    }

    public LmsException(String message,  String errorCode,HttpStatus httpStatus) {
        super(message);
        this.errorCode=errorCode;
        this.httpStatus=httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
