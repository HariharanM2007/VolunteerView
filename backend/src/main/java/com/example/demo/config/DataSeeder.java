package com.example.demo.config;

import com.example.demo.entity.SystemUser;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;

import com.example.demo.repository.OrganizationRepository;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.UserStatus;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SystemUserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OpportunityRepository opportunityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.existsByUsername("admin")) {
    return;
}

SystemUser admin = SystemUser.builder()
        .username("admin")
        .email("admin@volunteerview.com")
        .passwordHash(passwordEncoder.encode("Admin@123"))
        .fullName("Platform Administrator")
        .phoneNumber("9999999999")
        .role(Role.PLATFORM_ADMIN)
        .status(UserStatus.APPROVED)
        .isActive(true)
        .build();

userRepository.save(admin);

    }
}