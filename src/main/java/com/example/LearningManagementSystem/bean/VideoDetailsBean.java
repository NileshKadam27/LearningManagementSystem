package com.example.LearningManagementSystem.bean;

import java.util.List;

public class VideoDetailsBean {

	private String courseCategory;

	private CourseBean courseBean;

	private VideoBean videoBean;

	public String getCourseCategory() {
		return courseCategory;
	}

	public void setCourseCategory(String courseCategory) {
		this.courseCategory = courseCategory;
	}

	public CourseBean getCourseBean() {
		return courseBean;
	}

	public void setCourseBean(CourseBean courseBean) {
		this.courseBean = courseBean;
	}

	public VideoBean getVideoBean() {
		return videoBean;
	}

	public void setVideoBean(VideoBean videoBean) {
		this.videoBean = videoBean;
	}

}
