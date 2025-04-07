package com.example.UserProfileManager.controller;

import com.example.UserProfileManager.dto.UserProfileResponse;
import com.example.UserProfileManager.dto.UserProfileSimpleResponse;
import com.example.UserProfileManager.service.UserProfileService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProfileController {

    private final UserProfileService userProfileService;

    public ProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // v1 for  full profile with image
    @PostMapping(value = "/v1/api/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> createProfileV1(
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        UserProfileResponse response = userProfileService.createProfile(profileJson, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // v2 for Only name and email, no image
    @PostMapping(value = "/v2/api/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileSimpleResponse> createProfileV2(
            @RequestPart("profile") String profileJson) throws Exception {
        UserProfileSimpleResponse response = userProfileService.createProfileSimple(profileJson);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/api/profiles/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable Long id,
            @RequestPart("profile") String profileJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        UserProfileResponse response = userProfileService.updateProfile(id, profileJson, image);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/api/profiles/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> updateProfileImage(
            @PathVariable Long id,
            @RequestPart("image") MultipartFile image) throws Exception {
        UserProfileResponse response = userProfileService.updateProfileImage(id, image);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/v1/api/profiles")
    public ResponseEntity<Page<UserProfileResponse>> getAllProfilesV1(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit) {
        Page<UserProfileResponse> profiles = userProfileService.getAllProfiles(page, size, offset, limit);
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }


    @GetMapping("/v2/api/profiles")
    public ResponseEntity<Page<UserProfileSimpleResponse>> getAllProfilesV2(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit) {
        Page<UserProfileSimpleResponse> profiles = userProfileService.getAllProfilesSimple(page, size, offset, limit);
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    @GetMapping("/v1/api/profiles/{id}")
    public ResponseEntity<UserProfileResponse> getUserByIdV1(@PathVariable Long id) {
        UserProfileResponse response = userProfileService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v2/api/profiles/{id}")
    public ResponseEntity<UserProfileSimpleResponse> getUserByIdV2(@PathVariable Long id) {
        UserProfileSimpleResponse response = userProfileService.getUserByIdSimple(id);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/v1/api/profiles/{id}")
    public ResponseEntity<Void> deleteProfileV1(@PathVariable Long id) {
        userProfileService.deleteProfile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/v2/api/profiles/{id}")
    public ResponseEntity<Void> deleteProfileV2(@PathVariable Long id) {
        userProfileService.deleteProfile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}