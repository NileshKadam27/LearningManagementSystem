package com.example.LearningManagementSystem.bean;

import java.util.List;

public class CourseBean {

    private  Long courseId;

    private String courseName;

    private String professorName;

    private String experience;

    private String about;

    private String videoLink;
    
    
	private Long userprofilekey;
	
	private String coursedescription;
	
	private List<VideoBean> videoBean;

    private String courseImageLink;

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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

	public Long getUserprofilekey() {
		return userprofilekey;
	}

	public void setUserprofilekey(Long userprofilekey) {
		this.userprofilekey = userprofilekey;
	}

	public String getCoursedescription() {
		return coursedescription;
	}

	public void setCoursedescription(String coursedescription) {
		this.coursedescription = coursedescription;
	}

	public List<VideoBean> getVideoBean() {
		return videoBean;
	}

	public void setVideoBean(List<VideoBean> videoBean) {
		this.videoBean = videoBean;
	}

    public String getCourseImageLink() {
        return courseImageLink;
    }
    public void setCourseImageLink(String courseImageLink) {
        this.courseImageLink = courseImageLink;
    }
}
