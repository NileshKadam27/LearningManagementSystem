package com.example.LearningManagementSystem.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.LearningManagementSystem.entity.VideoDetails;

public interface VideoDetailsRepository extends MongoRepository<VideoDetails, String>{
	
	VideoDetails findByCourseId(Long courseKey);
	

}
