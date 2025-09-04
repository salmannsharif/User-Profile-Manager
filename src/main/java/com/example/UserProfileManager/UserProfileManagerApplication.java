package com.example.UserProfileManager;

import com.example.UserProfileManager.entity.UserProfile;
import com.example.UserProfileManager.repository.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@SpringBootApplication
public class UserProfileManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserProfileManagerApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserProfileRepository repo, PasswordEncoder encoder) {
		return args -> {
			if (repo.findByEmail("admin@example.com").isEmpty()) {
				UserProfile admin = new UserProfile();
				admin.setName("Admin User");
				admin.setRole("ADMIN");
				admin.setEmail("admin@example.com");
				admin.setPassword(encoder.encode("password")); // encode before saving
				admin.setAddress("Default Address");
				repo.save(admin);
				System.out.println("✅ Default admin user created: admin@example.com / password");
			}
		};
	}

}
