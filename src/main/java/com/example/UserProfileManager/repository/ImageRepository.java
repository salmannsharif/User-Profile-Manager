package com.example.UserProfileManager.repository;

import com.example.UserProfileManager.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}