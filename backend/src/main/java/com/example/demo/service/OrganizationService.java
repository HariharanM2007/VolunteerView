// package com.example.demo.service;

// import com.example.demo.dto.organization.OrganizationRequestDto;
// import com.example.demo.dto.organization.OrganizationResponseDto;
// import com.example.demo.entity.Opportunity;
// import com.example.demo.entity.Organization;
// import com.example.demo.entity.SystemUser;
// import com.example.demo.exception.BusinessValidationException;
// import com.example.demo.exception.ResourceNotFoundException;
// import com.example.demo.repository.OpportunityRepository;
// import com.example.demo.repository.OrganizationRepository;
// import com.example.demo.repository.SystemUserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;

// @Service
// @RequiredArgsConstructor
// public class OrganizationService {

//     private final OrganizationRepository organizationRepository;
//     private final SystemUserRepository systemUserRepository;
//     private final OpportunityRepository opportunityRepository;

//     public OrganizationResponseDto createOrganization(OrganizationRequestDto dto) {
//         SystemUser coordinator = systemUserRepository.findById(dto.getCoordinatorId())
//                 .orElseThrow(() -> new ResourceNotFoundException("SystemUser", dto.getCoordinatorId()));

//         if (coordinator.getRole() != SystemUser.Role.ORGANIZATION_COORDINATOR
//                 && coordinator.getRole() != SystemUser.Role.PLATFORM_ADMIN) {
//             throw new BusinessValidationException("User " + coordinator.getUsername() + " must have ORGANIZATION_COORDINATOR or PLATFORM_ADMIN role");
//         }

//         if (organizationRepository.existsByName(dto.getName())) {
//             throw new BusinessValidationException("An organization with name " + dto.getName() + " already exists");
//         }

//         Organization organization = Organization.builder()
//                 .name(dto.getName())
//                 .mission(dto.getMission())
//                 .address(dto.getAddress())
//                 .contactEmail(dto.getContactEmail())
//                 .contactPhone(dto.getContactPhone())
//                 .foundedYear(dto.getFoundedYear())
//                 .coordinator(coordinator)
//                 .status(Organization.OrganizationStatus.PENDING)
//                 .build();

//         Organization saved = organizationRepository.save(organization);
//         return mapToResponseDto(saved);
//     }

//     public OrganizationResponseDto updateOrganization(Long id, OrganizationRequestDto dto) {
//         Organization organization = organizationRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Organization", id));

//         organization.setName(dto.getName());
//         organization.setMission(dto.getMission());
//         organization.setAddress(dto.getAddress());
//         organization.setContactEmail(dto.getContactEmail());
//         organization.setContactPhone(dto.getContactPhone());
//         organization.setFoundedYear(dto.getFoundedYear());

//         Organization saved = organizationRepository.save(organization);
//         return mapToResponseDto(saved);
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public OrganizationResponseDto approveOrganization(Long id) {
//         Organization organization = organizationRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Organization", id));

//         if (organization.getStatus() != Organization.OrganizationStatus.PENDING) {
//             throw new BusinessValidationException("Organization " + organization.getName() + " cannot be approved. Current status: " + organization.getStatus() + ". Only PENDING organizations can be approved.");
//         }

//         organization.setStatus(Organization.OrganizationStatus.APPROVED);
//         Organization saved = organizationRepository.save(organization);
//         return mapToResponseDto(saved);
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public OrganizationResponseDto suspendOrganization(Long id) {
//         Organization organization = organizationRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Organization", id));

//         if (organization.getStatus() != Organization.OrganizationStatus.APPROVED) {
//             throw new BusinessValidationException("Organization " + organization.getName() + " cannot be suspended. Current status: " + organization.getStatus() + ". Only APPROVED organizations can be suspended.");
//         }

//         List<Opportunity> openOpportunities = opportunityRepository.findByOrganizationIdAndStatus(id, Opportunity.OpportunityStatus.OPEN);
//         for (Opportunity opportunity : openOpportunities) {
//             opportunity.setStatus(Opportunity.OpportunityStatus.CLOSED);
//         }
//         opportunityRepository.saveAll(openOpportunities);

//         organization.setStatus(Organization.OrganizationStatus.SUSPENDED);
//         Organization saved = organizationRepository.save(organization);
//         return mapToResponseDto(saved);
//     }

//     @Transactional(readOnly = true)
//     public Page<OrganizationResponseDto> getAllOrganizations(Pageable pageable) {
//         return organizationRepository.findAll(pageable).map(this::mapToResponseDto);
//     }

//     @Transactional(readOnly = true)
//     public OrganizationResponseDto getOrganizationById(Long id) {
//         Organization organization = organizationRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
//         return mapToResponseDto(organization);
//     }

//     @Transactional(readOnly = true)
//     public List<OrganizationResponseDto> getOrganizationsByCoordinator(Long coordinatorId) {
//         return organizationRepository.findAllByCoordinatorId(coordinatorId).stream()
//                 .map(this::mapToResponseDto)
//                 .toList();
//     }

//     public void deleteOrganization(Long id) {
//         Organization organization = organizationRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Organization", id));

//         long opportunityCount = opportunityRepository.countByOrganizationId(id);
//         if (opportunityCount > 0) {
//             throw new BusinessValidationException("Cannot delete organization " + organization.getName() + " because it has " + opportunityCount + " associated opportunities. Remove all opportunities first.");
//         }

//         organizationRepository.delete(organization);
//     }

//     private OrganizationResponseDto mapToResponseDto(Organization organization) {
//         return OrganizationResponseDto.builder()
//                 .id(organization.getId())
//                 .name(organization.getName())
//                 .mission(organization.getMission())
//                 .address(organization.getAddress())
//                 .contactEmail(organization.getContactEmail())
//                 .contactPhone(organization.getContactPhone())
//                 .foundedYear(organization.getFoundedYear())
//                 .status(organization.getStatus().name())
//                 .coordinatorId(organization.getCoordinator() != null ? organization.getCoordinator().getId() : null)
//                 .coordinatorName(organization.getCoordinator() != null ? organization.getCoordinator().getFullName() : null)
//                 .createdAt(organization.getCreatedAt())
//                 .updatedAt(organization.getUpdatedAt())
//                 .build();
//     }
// }
