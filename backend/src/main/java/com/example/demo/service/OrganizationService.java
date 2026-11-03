package com.example.demo.service;


import com.example.demo.enums.OpportunityStatus;
import com.example.demo.dto.OrganizationRequestDto;
import com.example.demo.dto.OrganizationResponseDto;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.OrganizationStatus;
import com.example.demo.enums.Role;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.repository.ImpactReportRepository;
import com.example.demo.repository.VolunteerEnrollmentRepository;
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

        private final ImpactReportRepository impactReportRepository;

        private final VolunteerEnrollmentRepository enrollmentRepository;

    @Transactional
        public OrganizationResponseDto createOrganization(
                        OrganizationRequestDto dto,
                        SystemUser actor
        ) {

        Long coordinatorId = dto.getCoordinatorId();
        if (actor.getRole() == Role.ORGANIZATION_COORDINATOR) {
                coordinatorId = actor.getId();
        }

        if (coordinatorId == null) {
                throw new BusinessValidationException("Coordinator ID is required.");
        }

        SystemUser coordinator = systemUserRepository.findById(coordinatorId)
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
                OrganizationRequestDto dto,
                SystemUser actor
) {

    Organization organization = organizationRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found.")
            );

    if (actor.getRole() == Role.ORGANIZATION_COORDINATOR
            && (organization.getCoordinator() == null
            || !organization.getCoordinator().getId().equals(actor.getId()))) {
        throw new BusinessValidationException(
                "Access Denied: You can only update your assigned organization."
        );
    }

        if (!organization.getName().equals(dto.getName())
                        && organizationRepository.existsByName(dto.getName())) {
                throw new BusinessValidationException(
                                "Organization name already exists."
                );
        }

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

    List<Opportunity> opportunities =
        opportunityRepository.findByOrganizationIdAndStatus(
                id,
                OpportunityStatus.OPEN
        );

for (Opportunity opportunity : opportunities) {

    opportunity.setStatus(OpportunityStatus.CLOSED);

    opportunityRepository.save(opportunity);

}

    organizationRepository.save(organization);

    return mapToResponseDto(organization);

    }


    @Transactional
    public Page<OrganizationResponseDto> getAllOrganizations(Pageable pageable) {

    return organizationRepository.findAll(pageable)
            .map(this::mapToResponseDto);

    }

    @Transactional
    public OrganizationResponseDto getOrganizationById(Long id) {

    Organization organization = organizationRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found.")
            );

    return mapToResponseDto(organization);

    }


    @Transactional
    public List<OrganizationResponseDto> getOrganizationsByCoordinator(
        Long coordinatorId
) {

    return organizationRepository.findAllByCoordinatorId(coordinatorId)
            .stream()
            .map(this::mapToResponseDto)
            .toList();

    }


    @Transactional
    public void deleteOrganization(Long id) {

    Organization organization = organizationRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found.")
            );

        for (Opportunity opportunity : opportunityRepository.findByOrganizationId(id)) {
                impactReportRepository.deleteByOpportunityId(opportunity.getId());
                enrollmentRepository.deleteByOpportunityId(opportunity.getId());
                opportunityRepository.delete(opportunity);
    }

        List<SystemUser> assignedUsers = systemUserRepository.findByOrganizationId(id);
        assignedUsers.forEach(user -> user.setOrganization(null));
        systemUserRepository.saveAll(assignedUsers);
    organizationRepository.delete(organization);

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