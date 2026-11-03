package com.example.demo.service;

import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.repository.ImpactReportRepository;
import com.example.demo.repository.VolunteerEnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    private final SystemUserRepository userRepository;

    private final OrganizationRepository organizationRepository;

        private final ImpactReportRepository impactReportRepository;

        private final VolunteerEnrollmentRepository enrollmentRepository;

        public UserService(
                        SystemUserRepository userRepository,
                        OrganizationRepository organizationRepository
        ) {
                this(userRepository, organizationRepository, null, null);
        }


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
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        SystemUser currentUser = getCurrentAuthenticatedUser();

        if (currentUser != null && currentUser.getId() != null
                && currentUser.getId().equals(user.getId())) {
            throw new BusinessValidationException(
                    "You cannot deactivate your own administrator account."
            );
        }

        user.setIsActive(false);
        user.setStatus(UserStatus.DEACTIVATED);
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

    @Transactional
    public UserResponseDto updateStatus(Long id, String statusStr) {

        SystemUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        UserStatus targetStatus = UserStatus.valueOf(statusStr.toUpperCase());

        SystemUser currentUser = getCurrentAuthenticatedUser();
        if (targetStatus == UserStatus.DEACTIVATED && currentUser != null
                && currentUser.getId() != null && currentUser.getId().equals(user.getId())) {
            throw new BusinessValidationException(
                    "You cannot deactivate your own administrator account."
            );
        }

        user.setStatus(targetStatus);

        if (targetStatus == UserStatus.DEACTIVATED) {
            user.setIsActive(false);
        }

        userRepository.save(user);

        return mapToResponseDto(user);
    }

        @Transactional
        public void deleteDeactivatedUser(Long userId) {
                SystemUser user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                if (user.getStatus() != UserStatus.DEACTIVATED || user.getIsActive()) {
                        throw new BusinessValidationException(
                                        "Only deactivated users can be deleted."
                        );
                }

                SystemUser currentAdmin = getCurrentAuthenticatedUser();
                if (currentAdmin == null || currentAdmin.getId().equals(userId)) {
                        throw new BusinessValidationException(
                                        "The current administrator cannot be deleted."
                        );
                }

                for (Organization organization : organizationRepository.findAllByCoordinatorId(userId)) {
                        organization.setCoordinator(currentAdmin);
                        organizationRepository.save(organization);
                }

                impactReportRepository.deleteByVolunteerId(userId);
                enrollmentRepository.deleteByVolunteerId(userId);
                user.setOrganization(null);
                userRepository.delete(user);
        }

    @Transactional
    public UserResponseDto assignOrganization(Long userId, Long orgId) {

        SystemUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgId));

        user.setOrganization(org);

        userRepository.save(user);

        return mapToResponseDto(user);
    }

    @Transactional
    public java.util.List<UserResponseDto> getAllUsersList() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private SystemUser getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof SystemUser user) {
            return user;
        }

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        }

        return null;
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