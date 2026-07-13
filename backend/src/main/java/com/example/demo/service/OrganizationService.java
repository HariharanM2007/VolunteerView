package com.example.demo.service;

import com.example.demo.dto.organization.OrganizationRequestDto;
import com.example.demo.dto.organization.OrganizationResponseDto;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.OrganizationStatus;
import com.example.demo.enums.Role;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final SystemUserRepository systemUserRepository;

    private final OpportunityRepository opportunityRepository;

    @Transactional
    public OrganizationResponseDto createOrganization(OrganizationRequestDto dto) {

    SystemUser coordinator = systemUserRepository.findById(dto.getCoordinatorId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Coordinator not found.")
            );

    if (coordinator.getRole() != Role.ORGANIZATION_COORDINATOR
            && coordinator.getRole() != Role.PLATFORM_ADMIN) {

        throw new BusinessValidationException(
                "User "
                        + coordinator.getUsername()
                        + " must have ORGANIZATION_COORDINATOR or PLATFORM_ADMIN role."
        );
    }

    if (organizationRepository.existsByName(dto.getName())) {

        throw new BusinessValidationException(
                "An organization with name "
                        + dto.getName()
                        + " already exists."
        );

    }

    Organization organization = Organization.builder()
            .name(dto.getName())
            .mission(dto.getMission())
            .address(dto.getAddress())
            .contactEmail(dto.getContactEmail())
            .contactPhone(dto.getContactPhone())
            .foundedYear(dto.getFoundedYear())
            .status(OrganizationStatus.PENDING)
            .coordinator(coordinator)
            .build();

    organizationRepository.save(organization);

    return mapToResponseDto(organization);

    }




    @Transactional
    public OrganizationResponseDto updateOrganization(
        Long id,
        OrganizationRequestDto dto
) {

    Organization organization = organizationRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found.")
            );

    organization.setName(dto.getName());
    organization.setMission(dto.getMission());
    organization.setAddress(dto.getAddress());
    organization.setContactEmail(dto.getContactEmail());
    organization.setContactPhone(dto.getContactPhone());
    organization.setFoundedYear(dto.getFoundedYear());

    organizationRepository.save(organization);

    return mapToResponseDto(organization);

    }



    @Transactional
public OrganizationResponseDto approveOrganization(Long id) {

    Organization organization = organizationRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found.")
            );

    if (organization.getStatus() != OrganizationStatus.PENDING) {

        throw new BusinessValidationException(
                "Organization "
                        + organization.getName()
                        + " cannot be approved. Current status: "
                        + organization.getStatus()
                        + ". Only PENDING organizations can be approved."
        );

    }

    organization.setStatus(OrganizationStatus.APPROVED);

    organizationRepository.save(organization);

    return mapToResponseDto(organization);

    }



    @Transactional(rollbackOn = Exception.class)
public OrganizationResponseDto suspendOrganization(Long id) {

    Organization organization = organizationRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found.")
            );

    if (organization.getStatus() != OrganizationStatus.APPROVED) {

        throw new BusinessValidationException(
                "Organization "
                        + organization.getName()
                        + " cannot be suspended. Current status: "
                        + organization.getStatus()
                        + ". Only APPROVED organizations can be suspended."
        );

    }

    organization.setStatus(OrganizationStatus.SUSPENDED);

    opportunityRepository
            .findByOrganizationIdAndStatus(
                    id,
                    com.example.demo.enums.OpportunityStatus.OPEN
            )
            .forEach(opportunity -> {
                opportunity.setStatus(
                        com.example.demo.enums.OpportunityStatus.CLOSED
                );
            });

    organizationRepository.save(organization);

    return mapToResponseDto(organization);

    }






    private OrganizationResponseDto mapToResponseDto(
        Organization organization
) {

    return OrganizationResponseDto.builder()
            .id(organization.getId())
            .name(organization.getName())
            .mission(organization.getMission())
            .address(organization.getAddress())
            .contactEmail(organization.getContactEmail())
            .contactPhone(organization.getContactPhone())
            .foundedYear(organization.getFoundedYear())
            .status(organization.getStatus().name())
            .coordinatorId(
                    organization.getCoordinator() != null
                            ? organization.getCoordinator().getId()
                            : null
            )
            .coordinatorName(
                    organization.getCoordinator() != null
                            ? organization.getCoordinator().getFullName()
                            : null
            )
            .createdAt(organization.getCreatedAt())
            .updatedAt(organization.getUpdatedAt())
            .build();

    }

}