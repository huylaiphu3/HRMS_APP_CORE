package com.hrms.config;

import com.hrms.user.entity.User;
import com.hrms.user.entity.UserRole;
import com.hrms.user.entity.UserStatus;
import com.hrms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {

        boolean exists = userRepository.findByUsername("admin").isPresent();

        if (exists) {
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setFullName("Full Name");

        admin.setUserRole(UserRole.ADMIN);
        admin.setUserStatus(UserStatus.ACTIVE);

        admin.setActive(true);
        admin.setFailedLoginAttempts(0);

        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        userRepository.save(admin);
    }
}