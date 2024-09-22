package com.example.LearningManagementSystem.service;

import java.util.List;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;

public interface CourseService {

    public List<CourseDetailsBean> getAllCourseDetails();

    public CourseDetailsBean getCourseDetailsByCategory(Long catid);

    public CourseBean getCourseDetailsById(Long courseid);
    
    public CourseDetailsBean uploadVideoDetails(CourseDetailsBean courseDetailsBean);
    
    public CourseBean updateVideoDetails(Long courseKey,Long videoId,CourseBean courseBean);


}
