package com.example.LearningManagementSystem.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.LearningManagementSystem.entity.VideoDetails;

public interface VideoDetailsRepository extends MongoRepository<VideoDetails, String>{
	
	VideoDetails findByCourseId(Long courseKey);
	
	VideoDetails findByCourseIdAndVideoId(Long courseKey,Long videoId);
	
	VideoDetails  findByvideoId(Long videoId);
	

}
