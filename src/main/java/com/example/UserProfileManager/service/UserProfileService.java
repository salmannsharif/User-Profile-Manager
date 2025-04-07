package com.example.UserProfileManager.service;

import com.example.UserProfileManager.dto.UserProfileResponse;
import com.example.UserProfileManager.dto.UserProfileSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    UserProfileResponse createProfile(String profileJson, MultipartFile image) throws Exception;
    UserProfileResponse updateProfile(Long id, String profileJson, MultipartFile image) throws Exception;
    Page<UserProfileResponse> getAllProfiles(int page, int size, int offset, int limit);
    UserProfileResponse getUserById(Long id);
    UserProfileResponse updateProfileImage(Long id, MultipartFile image) throws Exception;
    Page<UserProfileSimpleResponse> getAllProfilesSimple(int page, int size, int offset, int limit);
    UserProfileSimpleResponse getUserByIdSimple(Long id);
    UserProfileSimpleResponse createProfileSimple(String profileJson) throws Exception;
    void deleteProfile(Long id);
}