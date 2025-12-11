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

        User admin = new User();
        admin.setFullName("Administrator");
        admin.setEmail("admin@example.com");
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setActive(true);

        userRepository.save(admin);

        log.info("✔ Admin user created: username='admin', password='admin123'");
    }
}
