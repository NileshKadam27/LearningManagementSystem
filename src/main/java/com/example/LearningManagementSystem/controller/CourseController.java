package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.service.CourseService;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class CourseController {

    @Autowired
    CourseService courseService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/v1/course/details")
    public ResponseEntity<Object> getAllCourseDetails(){
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getAllCourseDetails());
        responseBean.setMessage("All Category & Courses Details.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }

    @GetMapping("/v1/course/category/{catid}")
    public ResponseEntity<Object> getCourseDetailsByCategory(@PathVariable Long catid){
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getCourseDetailsByCategory(catid));
        responseBean.setMessage("Courses Detail for given Category.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }

    @GetMapping("/v1/course/{courseid}")
    public ResponseEntity<Object> getCourseDetailsById(@PathVariable Long courseid){
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getCourseDetailsById(courseid));
        responseBean.setMessage("Course Details.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }
}