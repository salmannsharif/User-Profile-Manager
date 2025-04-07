package com.example.UserProfileManager.repository;

import com.example.UserProfileManager.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByEmail(String email);
    Page<UserProfile> findAll(Pageable pageable);
}