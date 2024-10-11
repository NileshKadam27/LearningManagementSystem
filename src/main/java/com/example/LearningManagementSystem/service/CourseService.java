package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.bean.ProfDetBean;
import com.example.LearningManagementSystem.entity.Enrollment;
import com.example.LearningManagementSystem.entity.UserVideoprogress;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.exception.LmsException;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface CourseService {

    public List<CourseDetailsBean> getAllCourseDetails() throws LmsException;

    public CourseDetailsBean getCourseDetailsByCategory(Long catid) throws Exception;

    public CourseBean getCourseDetailsById(Long courseid) throws Exception;
    
	public ProfDetBean uploadCourseDetails(ProfDetBean profDetBean);

	public ProfDetBean updateVideoDetails(Long courseKey, Long videoId,  ProfDetBean profDetBean);

	public List<CourseDetailsBean> getCoursesDetails();

    public List<CourseBean> getMyEnrolledCourses() throws Exception;
    
    public List<CourseBean> getCourseDetailsByName(String coursename);
    
    public Enrollment saveEnrollment(Enrollment enrollment) throws Exception;
    
    public UserVideoprogress saveUserVideoProgress(UserVideoprogress userVideoprogress, Long courseid) throws Exception;
    
    public ProfDetBean addVideoDetails(Long courseKey, ProfDetBean profDetBean) throws EntityDataNotFound;
}
