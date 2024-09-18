package com.example.LearningManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

}
