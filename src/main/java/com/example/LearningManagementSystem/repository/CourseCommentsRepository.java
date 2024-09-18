package com.example.LearningManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.CourseComments;


@Repository
public interface CourseCommentsRepository  extends JpaRepository<CourseComments, Long> {

}
