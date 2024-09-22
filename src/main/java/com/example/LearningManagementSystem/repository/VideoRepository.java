package com.example.LearningManagementSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

	Video findByCourseid(long courseKey);

	Video findByIdAndCourseid(Long id, Long courseKey);

	List<Video> findByCourseid(Long courseKey);

}
