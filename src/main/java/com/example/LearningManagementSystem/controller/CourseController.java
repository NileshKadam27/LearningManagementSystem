package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.service.CourseService;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class CourseController {

    @Autowired
    CourseService courseService;

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
    
    
    @PostMapping("/v1/user/professor/course/register")
    public ResponseEntity<Object> uploadVideoDetails(@RequestBody CourseDetailsBean courseDetailsBean){
        return ResponseHandler.responseEntity("upload Video Details",courseService.uploadVideoDetails(courseDetailsBean), HttpStatus.OK);
    }
    
    @PutMapping("/v1/user/professor/course/register/{courseKey}")
    public ResponseEntity<Object> updateVideoDetails(@PathVariable Long courseKey,@RequestParam Long videoId,
    		@RequestBody CourseBean courseBean){
        return ResponseHandler.responseEntity("update Video Details",courseService.updateVideoDetails(courseKey,videoId,courseBean), HttpStatus.OK);
    }
    
    
    
    @GetMapping("/v1/user/mycourse")
    public ResponseEntity<Object> getProfCourses(){
        return ResponseHandler.responseEntity("Courses Detail for given Category",courseService.getCoursesDetails(), HttpStatus.OK);
    }
}
