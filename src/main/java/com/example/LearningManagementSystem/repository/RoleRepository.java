package com.example.LearningManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LearningManagementSystem.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRolename(String rolename);
}
