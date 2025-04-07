package com.example.UserProfileManager.dto;

public class UserProfileSimpleResponse {

    private String name;
    private String email;

    public UserProfileSimpleResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}