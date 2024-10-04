package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.bean.ProfDetBean;
import com.example.LearningManagementSystem.exception.LmsException;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface CourseService {

    public List<CourseDetailsBean> getAllCourseDetails() throws LmsException;

    public CourseDetailsBean getCourseDetailsByCategory(Long catid) throws Exception;

    public CourseBean getCourseDetailsById(Long courseid) throws Exception;
    
	public ProfDetBean uploadVideoDetails(ProfDetBean profDetBean);

	public ProfDetBean updateVideoDetails(Long courseKey, Long videoId,  ProfDetBean profDetBean);

	public List<CourseDetailsBean> getCoursesDetails();

    public List<CourseBean> getMyEnrolledCourses(HttpHeaders headers) throws Exception;
}
