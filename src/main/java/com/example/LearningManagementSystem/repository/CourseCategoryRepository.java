package com.example.LearningManagementSystem.repository;

import com.example.LearningManagementSystem.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory,Long> {

}