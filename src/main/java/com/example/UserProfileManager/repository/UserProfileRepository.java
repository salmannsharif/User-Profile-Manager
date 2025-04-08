package com.example.UserProfileManager.repository;

import com.example.UserProfileManager.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByEmail(String email);
    Page<UserProfile> findAll(Pageable pageable);
    Optional<UserProfile> findByEmail(String email);
}