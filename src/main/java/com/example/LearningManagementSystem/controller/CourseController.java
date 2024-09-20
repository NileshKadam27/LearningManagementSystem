package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.service.CourseService;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CourseController {

    @Autowired
    CourseService courseService;

    //@Secured(value = { "ROLE_admin"})

    @PreAuthorize("hasRole('ROLE_student')")
    @GetMapping("/v1/course/details")
    public ResponseEntity<Object> getAllCourseDetails(){
        return ResponseHandler.responseEntity("All Courses Details",courseService.getAllCourseDetails(), HttpStatus.OK);
    }

    @GetMapping("/v1/course/category/{catid}")
    public ResponseEntity<Object> getCourseDetailsByCategory(@PathVariable Long catid){
        return ResponseHandler.responseEntity("Courses Detail for given Category",courseService.getCourseDetailsByCategory(catid), HttpStatus.OK);
    }

    @GetMapping("/v1/course/{courseid}")
    public ResponseEntity<Object> getCourseDetailsById(@PathVariable Long courseid){
        return ResponseHandler.responseEntity("Courses Detail for given Category",courseService.getCourseDetailsById(courseid), HttpStatus.OK);
    }
}
