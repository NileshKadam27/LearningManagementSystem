package com.example.LearningManagementSystem.repository;

import com.example.LearningManagementSystem.entity.CourseDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseDetailsRepository extends MongoRepository<CourseDetails,Long> {
    CourseDetails findByCourseid(String courseId);
}
