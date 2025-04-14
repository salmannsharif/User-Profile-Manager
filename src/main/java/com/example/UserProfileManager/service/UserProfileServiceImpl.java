package com.example.UserProfileManager.service;

import com.example.UserProfileManager.dto.*;
import com.example.UserProfileManager.entity.Image;
import com.example.UserProfileManager.entity.UserProfile;
import com.example.UserProfileManager.exception.ResourceNotFoundException;
import com.example.UserProfileManager.exception.ValidationException;
import com.example.UserProfileManager.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.renderer.DivRenderer;
import com.itextpdf.layout.renderer.DocumentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);
    private final UserProfileRepository repository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    public UserProfileServiceImpl(UserProfileRepository repository, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
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
        profile.setPassword(passwordEncoder.encode(request.getPassword())); // Use BCrypt
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
        profile.setPassword(passwordEncoder.encode("default_password")); // Use BCrypt
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
        existingProfile.setPassword(passwordEncoder.encode(request.getPassword())); // Use BCrypt
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
    @Transactional
    public Page<UserProfileResponse> getAllProfiles(int page, int size) {
        logger.info("Fetching profiles (v1) - Page: {}, Size: {}, Offset: {}, Limit: {}", page, size);
        Page<UserProfile> profilePage = repository.findAll(PageRequest.of(page, size));
        return profilePage.map(profile -> new UserProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getEmail(),
                profile.getAddress(),
                profile.getImage()
        ));
    }

    @Override
    @Transactional
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
    public Page<UserProfileSimpleResponse> getAllProfilesSimple(int page, int size) {
        logger.info("Fetching profiles (v2) - Page: {}, Size: {}, Offset: {}, Limit: {}", page, size);
        Page<UserProfile> profilePage = repository.findAll(PageRequest.of(page, size));
        return profilePage.map(profile -> new UserProfileSimpleResponse(
                profile.getName(),
                profile.getEmail()
        ));
    }

    @Override
//    @Transactional
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


    @Override
    public byte[] generateUserPdf(int page, int size) {
        logger.info("Generating PDF for users - Page: {}, Size: {}", page, size);
        Page<UserProfile> profilePage = repository.findAll(PageRequest.of(page, size));
        List<UserProfileResponse> users = mapUserProfiles(profilePage.get());
        long totalUsers = profilePage.getTotalElements();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Set custom renderer with border
            document.setRenderer(new CustomDocumentRenderer(document));

            // Existing content
            document.add(new Paragraph("User Profile Manager")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Total Users: " + totalUsers)
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            float[] columnWidths = {1, 3, 3, 3, 2}; // S.No, Name, Email, Address, Role
            Table table = new Table(columnWidths);
            table.setWidth(500);
            table.setMarginTop(20);

            table.addHeaderCell(new Cell().add(new Paragraph("S.No").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Name").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Address").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Role").setBold()));

            int startSerial = page * size + 1;
            for (UserProfileResponse user : users) {
                table.addCell(String.valueOf(startSerial++));
                table.addCell(user.getName());
                table.addCell(user.getEmail());
                table.addCell(user.getAddress() != null ? user.getAddress() : "N/A");
                table.addCell(user.getRole() != null ? user.getRole() : "N/A");
            }


            document.add(table);
        } catch (Exception e) {
            logger.error("Error generating PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF", e);
        }

        logger.info("PDF generated successfully for page: {}, size: {}", page, size);
        return baos.toByteArray();
    }

    @Override
    public byte[] generateAllUsersPdf() {
        logger.info("Generating PDF for all users");
        List<UserProfile> profiles = repository.findAll();
        List<UserProfileResponse> users = mapUserProfiles(profiles.stream());
        long totalUsers = users.size();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.setRenderer(new CustomDocumentRenderer(document));

            document.add(new Paragraph("User Profile Manager")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Total Users: " + totalUsers)
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            float[] columnWidths = {1, 3, 3, 3, 2}; // S.No, Name, Email, Address, Role
            Table table = new Table(columnWidths);
            table.setWidth(500);
            table.setMarginTop(20);

            table.addHeaderCell(new Cell().add(new Paragraph("S.No").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Name").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Address").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Role").setBold()));

            int startSerial = 1;
            for (UserProfileResponse user : users) {
                table.addCell(String.valueOf(startSerial++));
                table.addCell(user.getName());
                table.addCell(user.getEmail());
                table.addCell(user.getAddress() != null ? user.getAddress() : "N/A");
                table.addCell(user.getRole() != null ? user.getRole() : "N/A");
            }

            document.add(table);
        } catch (Exception e) {
            logger.error("Error generating PDF for all users: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF", e);
        }

        logger.info("PDF generated successfully for all users");
        return baos.toByteArray();
    }

    private static class CustomDocumentRenderer extends DocumentRenderer {
        public CustomDocumentRenderer(Document document) {
            super(document);
        }

        @Override
        protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
            LayoutArea area = super.updateCurrentArea(overflowResult); // Apply margins
            Rectangle newBBox = area.getBBox().clone();

            // Apply border (10 points from edges, adjustable)
            float[] borderWidths = {5, 5, 5, 5}; // left, top, right, bottom
            newBBox.applyMargins(borderWidths[0], borderWidths[1], borderWidths[2], borderWidths[3], false);

            // Add a background Div for the border
            Div div = new Div()
                    .setWidth(newBBox.getWidth())
                    .setHeight(newBBox.getHeight())
                    .setBorder(new SolidBorder(1)) // 1-point solid border
                    .setBackgroundColor(ColorConstants.WHITE); // Optional: Set background if needed
            addChild(new DivRenderer(div)); // Use DivRenderer instead of Div

            // Apply padding (optional, adjustable)
            float[] paddingWidths = {5, 5, 5, 5}; // left, top, right, bottom
            newBBox.applyMargins(paddingWidths[0], paddingWidths[1], paddingWidths[2], paddingWidths[3], false);

            return (currentArea = new RootLayoutArea(area.getPageNumber(), newBBox)); // Use RootLayoutArea
        }
    }

    private List<UserProfileResponse> mapUserProfiles(Stream<UserProfile> profileContent) {
        List<UserProfileResponse> users = profileContent
                .map(profile -> new UserProfileResponse(
                        profile.getId(),
                        profile.getName(),
                        profile.getEmail(),
                        profile.getAddress(),
                        profile.getRole()
                ))
                .collect(Collectors.toList());
        return users;
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