package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.bean.VideoDetailsBean;

import java.util.List;

public interface CourseService {

    public List<CourseDetailsBean> getAllCourseDetails();

    public CourseDetailsBean getCourseDetailsByCategory(Long catid);

    public CourseBean getCourseDetailsById(Long courseid);
    
    public VideoDetailsBean uploadVideoDetails(VideoDetailsBean videoDetailsBean);
}
