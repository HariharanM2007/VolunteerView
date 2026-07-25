package com.example.demo.service;

import com.example.demo.dto.AuthRequestDto;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.RefreshTokenRequestDto;
import com.example.demo.dto.RegisterDto;
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

    public AuthResponseDto login(AuthRequestDto dto) {

    SystemUser user = userRepository.findByUsername(dto.getUsername())
            .orElseThrow(() -> new BusinessValidationException(
                    "Invalid credentials. No account found."
            ));

    if (!user.getIsActive()) {
        throw new BusinessValidationException(
                "Your account has been deactivated. Please contact a platform administrator."
        );
    }

    if (user.getRole() == Role.PLATFORM_ADMIN
            && user.getStatus() == UserStatus.PENDING) {

        user.setStatus(UserStatus.APPROVED);
        userRepository.save(user);
    }

    if (user.getStatus() != UserStatus.APPROVED) {
        throw new BusinessValidationException(
                "Your account status is "
                        + user.getStatus()
                        + ". You can only log in once an administrator approves your account."
        );
    }

    if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
        throw new BusinessValidationException(
                "Invalid credentials. Incorrect password."
        );
    }

    String accessToken = jwtService.generateAccessToken(user);

    String refreshToken = jwtService.generateRefreshToken(user);

    return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .status(user.getStatus().name())
            .organizationId(
                    user.getOrganization() != null
                            ? user.getOrganization().getId()
                            : null
            )
            .organizationName(
                    user.getOrganization() != null
                            ? user.getOrganization().getName()
                            : null
            )
            .build();

    }

    public AuthResponseDto refreshToken(RefreshTokenRequestDto dto) {

    if (dto.getRefreshToken() == null || dto.getRefreshToken().isBlank()) {
        throw new BusinessValidationException(
                "Refresh token is required"
        );
    }

    String username;

    try {

        username = jwtService.extractUsername(dto.getRefreshToken());

    } catch (Exception ex) {

        throw new BusinessValidationException(
                "Invalid or expired refresh token"
        );

    }

    if (jwtService.isTokenExpired(dto.getRefreshToken())) {
        throw new BusinessValidationException(
                "Refresh token has expired. Please log in again."
        );
    }

    SystemUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found for refresh token"
            ));

    if (!user.getIsActive()) {
        throw new BusinessValidationException(
                "Account is deactivated. Cannot refresh token."
        );
    }

    String accessToken = jwtService.generateAccessToken(user);

    return AuthResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(dto.getRefreshToken())
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .status(user.getStatus().name())
            .organizationId(
                    user.getOrganization() != null
                            ? user.getOrganization().getId()
                            : null
            )
            .organizationName(
                    user.getOrganization() != null
                            ? user.getOrganization().getName()
                            : null
            )
            .build();

    }


}