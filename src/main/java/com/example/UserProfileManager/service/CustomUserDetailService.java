package com.example.UserProfileManager.service;

import com.example.UserProfileManager.entity.UserProfile;
import com.example.UserProfileManager.repository.UserProfileRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserProfileRepository repository;

    public CustomUserDetailService(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserProfile userProfile = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with this email :" + email));
        return userProfile;
    }
}