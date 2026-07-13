package com.example.demo.service;

import com.example.demo.dto.opportunity.OpportunityRequestDto;
import com.example.demo.dto.opportunity.OpportunityResponseDto;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.OpportunityStatus;
import com.example.demo.enums.Role;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.OrganizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;

    private final OrganizationRepository organizationRepository;

    @Transactional
public OpportunityResponseDto createOpportunity(
        OpportunityRequestDto dto,
        SystemUser actor
) {

    if (actor.getRole() == Role.ORGANIZATION_COORDINATOR) {

        if (actor.getOrganization() == null) {

            throw new BusinessValidationException(
                    "You must be assigned to an organization before creating opportunities."
            );

        }

        if (!actor.getOrganization().getId().equals(dto.getOrganizationId())) {

            throw new BusinessValidationException(
                    "Access Denied: You can only create opportunities for your assigned organization."
            );

        }

    }

    Organization organization = organizationRepository.findById(dto.getOrganizationId())
            .orElseThrow(() ->
                    new ResourceNotFoundException("Organization not found.")
            );

    Opportunity opportunity = Opportunity.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .location(dto.getLocation())
            .category(dto.getCategory())
            .scheduledDate(LocalDate.parse(dto.getScheduledDate()))
            .startTime(LocalTime.parse(dto.getStartTime()))
            .endTime(LocalTime.parse(dto.getEndTime()))
            .maxCapacity(dto.getMaxCapacity())
            .enrolledCount(0)
            .requiredSkills(dto.getRequiredSkills())
            .status(OpportunityStatus.DRAFT)
            .organization(organization)
            .build();

    opportunityRepository.save(opportunity);

    return mapToResponseDto(opportunity);

}

}