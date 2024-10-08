package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.bean.FeedbackBean;
import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.service.FeedbackService;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class FeedbackController {

    @Autowired
    FeedbackService feedBackService;




    @GetMapping("/v1/feedback/{courseid}")
    public ResponseEntity<Object> getFeedbackByCourseId(@PathVariable Long courseid, HttpHeaders headers) throws Exception{
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(feedBackService.getFeedbackByCourseId(courseid,headers));
        responseBean.setMessage("Feedback Detail for given course .");

        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }


    @PostMapping("/v1/feedback/addfeedback")
    public ResponseEntity<Object> addfeedback(@RequestBody FeedbackBean feedbackBean,HttpHeaders headers) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(feedBackService.addFeedback(feedbackBean,headers));
        responseBean.setMessage("Feedback saved successfully.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.CREATED);
    }








}
