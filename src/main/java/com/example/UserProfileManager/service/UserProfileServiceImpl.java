package com.example.UserProfileManager.service;

import com.example.UserProfileManager.dto.*;
import com.example.UserProfileManager.entity.Image;
import com.example.UserProfileManager.entity.UserProfile;
import com.example.UserProfileManager.exception.ResourceNotFoundException;
import com.example.UserProfileManager.exception.ValidationException;
import com.example.UserProfileManager.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);
    private final UserProfileRepository repository;
    private final ObjectMapper objectMapper;

    public UserProfileServiceImpl(UserProfileRepository repository, ObjectMapper objectMapper ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }


    @Override
    public UserProfileResponse createProfile(String profileJson, MultipartFile image) throws Exception {
        logger.info("Creating profile (v1) with JSON: {}", profileJson);

        UserProfileRequest request;
        try {
            request = objectMapper.readValue(profileJson, UserProfileRequest.class);
        } catch (Exception e) {
            logger.error("Failed to parse profile JSON: {}", e.getMessage());
            throw new ValidationException("Invalid profile JSON: " + e.getMessage());
        }

        validateRequest(request);

        if (repository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email " + request.getEmail() + " is already in use");
        }

        UserProfile profile = new UserProfile();
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setPassword(hashedPassword(request.getPassword()));
        profile.setAddress(request.getAddress());

        if (image != null && !image.isEmpty()) {
            validateImage(image);
            try {
                Image imageEntity = new Image();
                imageEntity.setFileName(image.getOriginalFilename());
                imageEntity.setData(image.getBytes());
                imageEntity.setExtension(getFileExtension(image.getOriginalFilename()));
                profile.setImage(imageEntity);
            } catch (Exception e) {
                logger.error("Error processing image: {}", e.getMessage());
                throw new ValidationException("Failed to process image: " + e.getMessage());
            }
        }

        UserProfile savedProfile = repository.save(profile);
        logger.info("Profile created with ID: {}", savedProfile.getId());
        return new UserProfileResponse(
                savedProfile.getId(),
                savedProfile.getName(),
                savedProfile.getEmail(),
                savedProfile.getAddress(),
                savedProfile.getImage()
        );
    }

    @Override
    public UserProfileSimpleResponse createProfileSimple(String profileJson) throws Exception {
        logger.info("Creating profile (v2) with JSON: {}", profileJson);

        UserProfileSimpleRequest request;
        try {
            request = objectMapper.readValue(profileJson, UserProfileSimpleRequest.class);
        } catch (Exception e) {
            logger.error("Failed to parse profile JSON: {}", e.getMessage());
            throw new ValidationException("Invalid profile JSON: " + e.getMessage());
        }

        validateSimpleRequest(request);

        if (repository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email " + request.getEmail() + " is already in use");
        }

        UserProfile profile = new UserProfile();
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setPassword(hashedPassword("default_password")); // Placeholder password
        profile.setAddress(null);

        UserProfile savedProfile = repository.save(profile);
        logger.info("Profile created (v2) with ID: {}", savedProfile.getId());
        return new UserProfileSimpleResponse(
                savedProfile.getName(),
                savedProfile.getEmail()
        );
    }

    @Override
    public UserProfileResponse updateProfile(Long id, String profileJson, MultipartFile image) throws Exception {
        logger.info("Attempting to update profile with ID: {}", id);

        UserProfile existingProfile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile with ID " + id + " not found"));

        UserProfileRequest request;
        try {
            request = objectMapper.readValue(profileJson, UserProfileRequest.class);
        } catch (Exception e) {
            logger.error("Failed to parse profile JSON: {}", e.getMessage());
            throw new ValidationException("Invalid profile JSON: " + e.getMessage());
        }

        validateRequest(request);

        if (!existingProfile.getEmail().equals(request.getEmail()) && repository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email " + request.getEmail() + " is already in use");
        }

        existingProfile.setName(request.getName());
        existingProfile.setEmail(request.getEmail());
        existingProfile.setPassword(hashedPassword(request.getPassword()));
        existingProfile.setAddress(request.getAddress());

        if (image != null && !image.isEmpty()) {
            validateImage(image);
            try {
                Image imageEntity = existingProfile.getImage() != null ? existingProfile.getImage() : new Image();
                imageEntity.setFileName(image.getOriginalFilename());
                imageEntity.setData(image.getBytes());
                imageEntity.setExtension(getFileExtension(image.getOriginalFilename()));
                existingProfile.setImage(imageEntity);
            } catch (Exception e) {
                logger.error("Error processing image: {}", e.getMessage());
                throw new ValidationException("Failed to process image: " + e.getMessage());
            }
        }

        UserProfile updatedProfile = repository.save(existingProfile);
        logger.info("Profile updated for ID: {}", updatedProfile.getId());
        return new UserProfileResponse(
                updatedProfile.getId(),
                updatedProfile.getName(),
                updatedProfile.getEmail(),
                updatedProfile.getAddress(),
                updatedProfile.getImage()
        );
    }

    @Override
    public Page<UserProfileResponse> getAllProfiles(int page, int size, int offset, int limit) {
        logger.info("Fetching profiles (v1) - Page: {}, Size: {}, Offset: {}, Limit: {}", page, size, offset, limit);

        if (page < 0) {
            throw new ValidationException("Page must be non-negative");
        }
        if (size <= 0) {
            throw new ValidationException("Size must be positive");
        }
        if (offset < 0) {
            throw new ValidationException("Offset must be non-negative");
        }
        if (limit <= 0) {
            throw new ValidationException("Limit must be positive");
        }

        Pageable pageable;
        if (offset != 0 || limit != 5 || (offset % limit == 0 && limit != size)) {
            int calculatedPage = offset / limit;
            pageable = PageRequest.of(calculatedPage, limit);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<UserProfile> profilePage = repository.findAll(pageable);
        return profilePage.map(profile -> new UserProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getEmail(),
                profile.getAddress(),
                profile.getImage()
        ));
    }

    @Override
    public UserProfileResponse getUserById(Long id) {
        logger.info("Fetching profile (v1) with ID: {}", id);

        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile with ID " + id + " not found"));

        return new UserProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getEmail(),
                profile.getAddress(),
                profile.getImage()
        );
    }

    @Override
    public Page<UserProfileSimpleResponse> getAllProfilesSimple(int page, int size, int offset, int limit) {
        logger.info("Fetching profiles (v2) - Page: {}, Size: {}, Offset: {}, Limit: {}", page, size, offset, limit);

        if (page < 0) {
            throw new ValidationException("Page must be non-negative");
        }
        if (size <= 0) {
            throw new ValidationException("Size must be positive");
        }
        if (offset < 0) {
            throw new ValidationException("Offset must be non-negative");
        }
        if (limit <= 0) {
            throw new ValidationException("Limit must be positive");
        }

        Pageable pageable;
        if (offset != 0 || limit != 5 || (offset % limit == 0 && limit != size)) {
            int calculatedPage = offset / limit;
            pageable = PageRequest.of(calculatedPage, limit);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<UserProfile> profilePage = repository.findAll(pageable);
        return profilePage.map(profile -> new UserProfileSimpleResponse(
                profile.getName(),
                profile.getEmail()
        ));
    }

    @Override
    public UserProfileSimpleResponse getUserByIdSimple(Long id) {
        logger.info("Fetching profile (v2) with ID: {}", id);

        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile with ID " + id + " not found"));

        return new UserProfileSimpleResponse(
                profile.getName(),
                profile.getEmail()
        );
    }

    @Override
    public UserProfileResponse updateProfileImage(Long id, MultipartFile image) throws Exception {
        logger.info("Updating image for profile with ID: {}", id);

        UserProfile existingProfile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile with ID " + id + " not found"));

        if (image != null && !image.isEmpty()) {
            validateImage(image);
            try {
                Image imageEntity = existingProfile.getImage() != null ? existingProfile.getImage() : new Image();
                imageEntity.setFileName(image.getOriginalFilename());
                imageEntity.setData(image.getBytes());
                imageEntity.setExtension(getFileExtension(image.getOriginalFilename()));
                existingProfile.setImage(imageEntity);
            } catch (Exception e) {
                logger.error("Error processing image: {}", e.getMessage());
                throw new ValidationException("Failed to process image: " + e.getMessage());
            }
        }

        UserProfile updatedProfile = repository.save(existingProfile);
        logger.info("Image updated for profile ID: {}", updatedProfile.getId());
        return new UserProfileResponse(
                updatedProfile.getId(),
                updatedProfile.getName(),
                updatedProfile.getEmail(),
                updatedProfile.getAddress(),
                updatedProfile.getImage()
        );
    }

    @Override
    public void deleteProfile(Long id) {
        logger.info("Attempting to delete profile with ID: {}", id);

        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile with ID " + id + " not found"));

        repository.delete(profile);
        logger.info("Profile deleted with ID: {}", id);
    }

    public String hashedPassword(String password){
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    private void validateImage(MultipartFile image) {
        String contentType = image.getContentType();
        String extension = getFileExtension(image.getOriginalFilename());
        logger.debug("Validating image - Content-Type: {}, Extension: {}", contentType, extension);

        if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            return; // Valid MIME type
        } else if (contentType == null || contentType.equals("application/octet-stream")) {
            if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                return; // Valid extension
            }
        }
        throw new ValidationException("Only JPEG and PNG images are allowed (Content-Type: " + contentType + ", Extension: " + extension + ")");
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private void validateRequest(UserProfileRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Name must not be blank");
        }
        if (request.getEmail() == null || request.getEmail().isBlank() || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Email must be a valid format");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("Password is required");
        }
    }

    private void validateSimpleRequest(UserProfileSimpleRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ValidationException("Name must not be blank");
        }
        if (request.getEmail() == null || request.getEmail().isBlank() || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Email must be a valid format");
        }
    }
}