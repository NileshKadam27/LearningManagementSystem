package com.example.LearningManagementSystem.bean;

import java.util.List;

public class CourseCat {

    private  Long catid;

    private String catgeoryName;

    private List<Course> courseList;

    public Long getCatid() {
        return catid;
    }

    public void setCatid(Long catid) {
        this.catid = catid;
    }

    public String getCatgeoryName() {
        return catgeoryName;
    }

    public void setCatgeoryName(String catgeoryName) {
        this.catgeoryName = catgeoryName;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
}
