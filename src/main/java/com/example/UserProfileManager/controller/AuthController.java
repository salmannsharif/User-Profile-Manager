package com.example.UserProfileManager.controller;

import com.example.UserProfileManager.dto.LoginRequest;
import com.example.UserProfileManager.security.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserDetailsService userDetailsService; // Now UserAuthService
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    public AuthController(UserDetailsService userDetailsService, AuthenticationManager authenticationManager, TokenManager tokenManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getUsername());
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        final String accessToken = tokenManager.generateToken((UserDetails) authenticate.getPrincipal());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        logger.info("Login successful for email: {}", loginRequest.getUsername());
        return ResponseEntity.ok(tokens);
    }
}