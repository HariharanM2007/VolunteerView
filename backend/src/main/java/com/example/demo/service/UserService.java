package com.example.demo.service;

import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SystemUserRepository systemUserRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return systemUserRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public UserResponseDto updateStatus(Long id, String statusStr) {
        SystemUser user = systemUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SystemUser", id));

        user.setStatus(SystemUser.UserStatus.valueOf(statusStr.toUpperCase()));

        SystemUser saved = systemUserRepository.save(user);
        return mapToResponseDto(saved);
    }

    @Transactional
    public UserResponseDto assignOrganization(Long userId, Long orgId) {
        SystemUser user = systemUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgId));

        user.setOrganization(organization);

        SystemUser saved = systemUserRepository.save(user);
        return mapToResponseDto(saved);
    }

    private UserResponseDto mapToResponseDto(SystemUser user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .organizationId(user.getOrganization() != null ? user.getOrganization().getId() : null)
                .organizationName(user.getOrganization() != null ? user.getOrganization().getName() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
