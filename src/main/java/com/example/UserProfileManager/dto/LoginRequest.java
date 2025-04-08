package com.example.UserProfileManager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @Email
    private String username;

    @NotBlank(message = "password required")
    private String password;

    public @Email String getUsername() {
        return username;
    }

    public void setUsername(@Email String username) {
        this.username = username;
    }

    public @NotBlank(message = "password required") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "password required") String password) {
        this.password = password;
    }

}
