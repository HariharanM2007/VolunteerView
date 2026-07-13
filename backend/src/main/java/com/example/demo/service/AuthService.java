package com.example.demo.service;

import com.example.demo.dto.auth.AuthRequestDto;
import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.auth.RefreshTokenRequestDto;
import com.example.demo.dto.auth.RegisterDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SystemUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthResponseDto register(RegisterDto dto) {

    if (userRepository.existsByUsername(dto.getUsername())) {
        throw new BusinessValidationException(
                "Username " + dto.getUsername()
                        + " is already taken. Please choose a different username."
        );
    }

    if (userRepository.existsByEmail(dto.getEmail())) {
        throw new BusinessValidationException(
                "Email " + dto.getEmail()
                        + " is already registered."
        );
    }

    Role role;

    try {
        role = Role.valueOf(dto.getRole().toUpperCase());
    } catch (IllegalArgumentException ex) {

        throw new BusinessValidationException(
                "Invalid role " + dto.getRole()
                        + ". Allowed roles: PLATFORM_ADMIN, ORGANIZATION_COORDINATOR, VOLUNTEER"
        );

    }

    SystemUser user = SystemUser.builder()
            .username(dto.getUsername())
            .email(dto.getEmail())
            .passwordHash(passwordEncoder.encode(dto.getPassword()))
            .fullName(dto.getFullName())
            .phoneNumber(
                    dto.getPhoneNumber() == null || dto.getPhoneNumber().isBlank()
                            ? null
                            : dto.getPhoneNumber()
            )
            .role(role)
            .status(UserStatus.PENDING)
            .isActive(true)
            .build();

    userRepository.save(user);

    return AuthResponseDto.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .status(user.getStatus().name())
            .build();

    }



}