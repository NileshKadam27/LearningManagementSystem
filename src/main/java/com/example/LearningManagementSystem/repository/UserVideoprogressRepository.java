package com.example.LearningManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.UserVideoprogress;

@Repository
public interface UserVideoprogressRepository  extends JpaRepository<UserVideoprogress, Long>{

}
