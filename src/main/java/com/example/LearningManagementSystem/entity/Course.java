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
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long coursecategorykey;
	
	private String coursename;
	
	private Long userprofilekey;
	
	private String coursedescription;

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

	public Long getCoursecategorykey() {
		return coursecategorykey;
	}

	public void setCoursecategorykey(Long coursecategorykey) {
		this.coursecategorykey = coursecategorykey;
	}

	public String getCoursename() {
		return coursename;
	}

	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}

	public Long getUserprofilekey() {
		return userprofilekey;
	}

	public void setUserprofilekey(Long userprofilekey) {
		this.userprofilekey = userprofilekey;
	}

	public String getCoursedescription() {
		return coursedescription;
	}

	public void setCoursedescription(String coursedescription) {
		this.coursedescription = coursedescription;
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
