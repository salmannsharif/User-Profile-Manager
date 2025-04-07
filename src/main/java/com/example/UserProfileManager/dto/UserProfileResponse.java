package com.example.UserProfileManager.dto;

import com.example.UserProfileManager.entity.Image;

import java.util.Base64;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String address;
    private String image;

    public UserProfileResponse(Long id, String name, String email, String address, Image image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.image = (image != null && image.getData() != null) ? Base64.getEncoder().encodeToString(image.getData()) : null;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }
}