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
@Table(name = "uservideoprogress")

public class UserVideoprogress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long videoid;

	private Long userkey;

	private String videoduration;

	private String status;

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

	public Long getVideoid() {
		return videoid;
	}

	public void setVideoid(Long videoid) {
		this.videoid = videoid;
	}

	public Long getUserkey() {
		return userkey;
	}

	public void setUserkey(Long userkey) {
		this.userkey = userkey;
	}

	public String getVideoduration() {
		return videoduration;
	}

	public void setVideoduration(String videoduration) {
		this.videoduration = videoduration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
