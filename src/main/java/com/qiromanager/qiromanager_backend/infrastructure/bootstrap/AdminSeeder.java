package com.qiromanager.qiromanager_backend.infrastructure.bootstrap;

import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedAdmin() {

        String adminUsername = "admin";

        boolean exists = userRepository.existsByUsername(adminUsername);

        if (exists) {
            log.info("Admin user already exists. Skipping seeding.");
            return;
        }

        User admin = User.create(
                "Administrator",
                adminUsername,
                "admin@example.com",
                passwordEncoder.encode("admin123"),
                Role.ADMIN
        );

        userRepository.save(admin);

        log.info("✔ Admin user created: username='admin', password='admin123'");
    }
}
