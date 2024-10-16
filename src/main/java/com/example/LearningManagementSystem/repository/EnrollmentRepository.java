package com.example.LearningManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.Enrollment;

import java.util.List;


@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByIdAndIsactive(Long userKey, int i);

    List<Enrollment> findByUserkeyAndIsactive(Long userId, int i);

    Enrollment findByCourseidAndUserkeyAndIsactive(Long courseid, Long userId, int i);

    List<Enrollment> findByCourseidAndIsactive(Long id, int i);
}
