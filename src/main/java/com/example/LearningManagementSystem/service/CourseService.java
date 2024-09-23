package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.exception.LmsException;

import java.util.List;

public interface CourseService {

    public List<CourseDetailsBean> getAllCourseDetails() throws LmsException;

    public CourseDetailsBean getCourseDetailsByCategory(Long catid) throws Exception;

    public CourseBean getCourseDetailsById(Long courseid) throws Exception;
    
	public CourseDetailsBean uploadVideoDetails(CourseDetailsBean courseDetailsBean);

	public CourseBean updateVideoDetails(Long courseKey, Long videoId, CourseBean courseBean);

	public List<CourseDetailsBean> getCoursesDetails();
}
