package com.example.LearningManagementSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.VideoDetails;

@Repository
public interface VideoDetailsRepository extends MongoRepository<VideoDetails, String>{
	
	List<VideoDetails> findByCourseId(Long courseKey);
	
	VideoDetails findByCourseIdAndVideoId(Long courseKey,Long videoId);
	
	Optional<VideoDetails> findByvideoId(Long videoId);
	

}
