package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.service.CourseService;
import com.example.LearningManagementSystem.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    @GetMapping("/v1/course/details")
    public ResponseEntity<Object> getAllCourseDetails() throws Exception {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getAllCourseDetails());
        responseBean.setMessage("All Category & Courses Details.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    @GetMapping("/v1/course/category/{catid}")
    public ResponseEntity<Object> getCourseDetailsByCategory(@PathVariable Long catid) throws Exception{
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getCourseDetailsByCategory(catid));
        responseBean.setMessage("Courses Detail for given Category.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    @GetMapping("/v1/course/{courseid}")
    public ResponseEntity<Object> getCourseDetailsById(@PathVariable Long courseid) throws Exception{
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getCourseDetailsById(courseid));
        responseBean.setMessage("Course Details.");
        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }

    //changed requestBody to ModelAttribute
    @PostMapping("/v1/user/professor/course/register")
    public ResponseEntity<Object> uploadVideoDetails(@ModelAttribute CourseDetailsBean courseDetailsBean){
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


    @GetMapping("/v1/course/enrolled")
    public ResponseEntity<Object> getMyEnrolledCourses(HttpHeaders headers) throws Exception{
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getMyEnrolledCourses(headers));
        responseBean.setMessage("Feedback Detail for given course .");

        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }

    
    
}
