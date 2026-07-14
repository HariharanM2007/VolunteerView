package com.example.demo.service;

import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SystemUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SystemUserRepository userRepository;


    @Transactional
public UserResponseDto approveUser(Long userId) {

    SystemUser user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found.")
            );

    user.setStatus(UserStatus.APPROVED);

    userRepository.save(user);

    return mapToResponseDto(user);

    }

    @Transactional
public UserResponseDto deactivateUser(Long userId) {

    SystemUser user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found.")
            );

    user.setIsActive(false);

    userRepository.save(user);

    return mapToResponseDto(user);

    }

    @Transactional
public Page<UserResponseDto> getAllUsers(Pageable pageable) {

    return userRepository.findAll(pageable)
            .map(this::mapToResponseDto);

    }

    @Transactional
public UserResponseDto getUserById(Long id) {

    SystemUser user = userRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found.")
            );

    return mapToResponseDto(user);

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
            .createdAt(user.getCreatedAt())
            .build();

    }

}