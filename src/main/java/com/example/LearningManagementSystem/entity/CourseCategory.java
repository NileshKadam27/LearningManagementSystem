package com.example.LearningManagementSystem.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "coursecategory")
public class CourseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryname;

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

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
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