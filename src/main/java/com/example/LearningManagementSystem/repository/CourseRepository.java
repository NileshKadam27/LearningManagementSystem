package com.example.LearningManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.Course;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByIsactive(Integer isActive);

    Course findByIdAndIsactive(Long courseid, Integer isActive);
    
    List<Course> findByUserprofilekey(Long userProfKey);
    
    List<Course> findByCoursecategorykey(Long catKey);
    
    @Query("SELECT e FROM Course e WHERE LOWER(e.coursename) LIKE LOWER(CONCAT('%', ?1, '%')) AND e.isactive = ?2")
    List<Course> findByCoursenameAndIsactive(String coursename, Integer isActive);
    
    List<Course> findByUserprofilekeyAndId(Long userProfKey, Long id);
    
    
    
}
