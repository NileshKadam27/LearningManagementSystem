package com.example.LearningManagementSystem.controller;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.ProfDetBean;
import com.example.LearningManagementSystem.bean.ResponseBean;
import com.example.LearningManagementSystem.entity.Enrollment;
import com.example.LearningManagementSystem.entity.UserVideoprogress;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.service.CourseService;
import com.example.LearningManagementSystem.utils.ResponseHandler;

import java.util.List;

import org.aspectj.lang.annotation.RequiredTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/v1/user/professor/course/register")
    public ResponseEntity<Object> uploadCourseDetails(ProfDetBean profDetBean){
        return ResponseHandler.responseEntity("upload Video Details",courseService.uploadCourseDetails(profDetBean), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/v1/user/professor/course/{courseKey}/video/{videoId}")
    public ResponseEntity<Object> updateVideoDetails(@PathVariable Long courseKey,@PathVariable Long videoId,
    		 ProfDetBean profDetBean){
        return ResponseHandler.responseEntity("update Video Details",courseService.updateVideoDetails(courseKey,videoId,profDetBean), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @GetMapping("/v1/user/professor/course/mycourse")
    public ResponseEntity<Object> getAllCourses(@RequestParam(required = false) Long courseId ){
    	return new  ResponseEntity<Object>(courseService.getCoursesDetails(courseId), HttpStatus.OK);
    }
    
    @GetMapping("/v1/course/{coursename}")
  	public ResponseEntity<Object> getCourseDetailsByName(@PathVariable String coursename) {
  		List<CourseBean> coursebeanlist = courseService.getCourseDetailsByName(coursename);
  		if (coursebeanlist != null) {
  			return ResponseHandler.responseEntity("All Matching Course Details Retrieved Successfully", coursebeanlist,
  					HttpStatus.OK);
  		} else {
  			return ResponseEntity.notFound().build();
  		}
  	}

    @PreAuthorize("hasAnyRole('STUDENT','INSTRUCTOR')")
    @GetMapping("/v1/course/enrolled")
    public ResponseEntity<Object> getMyEnrolledCourses() throws Exception{
        ResponseBean responseBean = new ResponseBean();
        responseBean.setPayload(courseService.getMyEnrolledCourses());
        responseBean.setMessage("Feedback Detail for given course .");

        return ResponseHandler.responseEntity(responseBean, HttpStatus.OK);
    }
    
    @PostMapping("/v1/user/enroll/course")
	public ResponseEntity<Object> enrollCourse(@RequestBody Enrollment enrollment) throws Exception {
		return ResponseHandler.responseEntity("Course Enrolled Successfully", courseService.saveEnrollment(enrollment), HttpStatus.OK);
	}
    
	@PostMapping("/v1/user/course/{courseid}")
	public ResponseEntity<Object> saveVideoProgress(@RequestBody UserVideoprogress userVideoprogress,
			@PathVariable Long courseid) throws Exception {
		return ResponseHandler.responseEntity("User video progress updated successfully",
				courseService.saveUserVideoProgress(userVideoprogress, courseid), HttpStatus.OK);
	}

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
	@PutMapping("/v1/user/professor/course/{courseKey}/video")
    public ResponseEntity<Object> AddVideoDetails(@PathVariable Long courseKey,
    		 ProfDetBean profDetBean) throws EntityDataNotFound{
        return ResponseHandler.responseEntity("update Video Details",courseService.addVideoDetails(courseKey,profDetBean), HttpStatus.OK);
    }
      
    
}
