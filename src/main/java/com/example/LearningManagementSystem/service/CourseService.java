package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.*;
import com.example.LearningManagementSystem.entity.Enrollment;
import com.example.LearningManagementSystem.entity.UserVideoprogress;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.exception.LmsException;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface CourseService {

    public List<CourseBean> getAllCourseDetails() throws LmsException;

    public CourseDetailsBean getCourseDetailsByCategory(Long catid) throws Exception;

    public CourseBean getCourseDetailsById(Long courseid) throws Exception;
    
	public ProfDetBean uploadCourseDetails(ProfDetBean profDetBean);

	public ProfDetBean updateVideoDetails(Long courseKey, Long videoId,  ProfDetBean profDetBean);

	public List<CourseDetailsBean> getCoursesDetails(Long courseId);

    public List<CourseBean> getMyEnrolledCourses() throws Exception;
    
    public List<CourseBean> getCourseDetailsByName(String coursename);
    
    public Enrollment saveEnrollment(Enrollment enrollment) throws Exception;
    
    public UserVideoprogress saveUserVideoProgress(UserVideoprogress userVideoprogress, Long courseid) throws Exception;
    
    public ProfDetBean addVideoDetails(Long courseKey, ProfDetBean profDetBean) throws EntityDataNotFound;

    public  List<CourseCat> getAllCourseCategory() throws Exception;

    public CourseBean getCourseById(Long courseId) throws Exception;

    public List<CourseBean> getCourses() throws Exception;

    public List<Dashboard> getDashboardDetails() throws Exception;
}
