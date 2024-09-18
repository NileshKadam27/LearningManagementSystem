package com.example.LearningManagementSystem.bean;

import java.util.List;

public class CourseDetailsBean {

    private String courseCategory;

    private List<CourseDetailsBean> courseDetailList;

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public List<CourseDetailsBean> getCourseDetailList() {
        return courseDetailList;
    }

    public void setCourseDetailList(List<CourseBean> courseDetailList) {
        this.courseDetailList = courseDetailList;
    }
}
