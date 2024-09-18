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
@Table(name = "coursecomments")
public class CourseComments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long enrollmentid;
	
	private String comments;
	
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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