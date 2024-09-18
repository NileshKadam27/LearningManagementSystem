package com.example.LearningManagementSystem.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedbacks")
public class Feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long enrollmentid;

	private String feedbackcomments;

	private String rating;

	private Integer isactive;

	@CreationTimestamp
	private LocalDateTime createddt;

	@UpdateTimestamp
	private LocalDateTime updateddt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEnrollmentid() {
		return enrollmentid;
	}

	public void setEnrollmentid(Long enrollmentid) {
		this.enrollmentid = enrollmentid;
	}

	public String getFeedbackcomments() {
		return feedbackcomments;
	}

	public void setFeedbackcomments(String feedbackcomments) {
		this.feedbackcomments = feedbackcomments;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public Integer getIsactive() {
		return isactive;
	}

	public void setIsactive(Integer isactive) {
		this.isactive = isactive;
	}

	public LocalDateTime getCreateddt() {
		return createddt;
	}

	public void setCreateddt(LocalDateTime createddt) {
		this.createddt = createddt;
	}

	public LocalDateTime getUpdateddt() {
		return updateddt;
	}

	public void setUpdateddt(LocalDateTime updateddt) {
		this.updateddt = updateddt;
	}

}
