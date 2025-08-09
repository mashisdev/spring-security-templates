package com.jwt.roles.config;

import com.jwt.roles.user.entity.Role;
import com.jwt.roles.user.entity.User;
import com.jwt.roles.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Checking for an existing ADMIN user...");

        Optional<User> adminUser = userRepository.findByEmail("admin@example.com");

        if (adminUser.isEmpty()) {
            log.info("No ADMIN user found. Creating a new one...");

            User newAdmin = User.builder()
                    .firstname("Admin")
                    .lastname("User")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(newAdmin);
            log.info("New ADMIN user created successfully.");
        } else {
            log.info("ADMIN user already exists. Skipping creation.");
        }
    }
}
