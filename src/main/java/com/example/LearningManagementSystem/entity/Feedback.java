package com.example.LearningManagementSystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "feedbacks")
public class Feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long courseid;

	private String feedbackcomments;

	private String rating;

	private Integer isactive;

	private Long userKey;

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

	public Long getCourseid() {
		return courseid;
	}

	public void setCourseid(Long courseid) {
		this.courseid = courseid;
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

	public Long getUserKey() {
		return userKey;
	}

	public void setUserKey(Long userKey) {
		this.userKey = userKey;
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
