package com.example.LearningManagementSystem.bean;

import java.util.List;

public class CourseBean {

    private  Long courseId;

    private String courseName;

    private String professorName;

    private String experience;
     
	private Long userprofilekey;
	
	private String coursedescription;
	
	private List<VideoBean> videoBean;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

	public Long getUserprofilekey() {
		return userprofilekey;
	}

	public void setUserprofilekey(Long userprofilekey) {
		this.userprofilekey = userprofilekey;
	}


	public List<VideoBean> getVideoBean() {
		return videoBean;
	}

	public void setVideoBean(List<VideoBean> videoBean) {
		this.videoBean = videoBean;
	}

	public String getCoursedescription() {
		return coursedescription;
	}

	public void setCoursedescription(String coursedescription) {
		this.coursedescription = coursedescription;
	}  
    
}
