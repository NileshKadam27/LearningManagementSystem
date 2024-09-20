package com.example.LearningManagementSystem.bean;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public class VideoBean {

	private String videoLink;

	private String videoTitle;

	private String videoDuration;

	private MultipartFile videoFile;

	private Long profKey;

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public String getVideoDuration() {
		return videoDuration;
	}

	public void setVideoDuration(String videoDuration) {
		this.videoDuration = videoDuration;
	}

	public MultipartFile getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(MultipartFile videoFile) {
		this.videoFile = videoFile;
	}

	public Long getProfKey() {
		return profKey;
	}

	public void setProfKey(Long profKey) {
		this.profKey = profKey;
	}

}
