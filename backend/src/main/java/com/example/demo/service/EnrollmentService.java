package com.example.demo.service;

import com.example.demo.dto.enrollment.EnrollmentRequestDto;
import com.example.demo.dto.enrollment.EnrollmentResponseDto;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.SystemUser;
import com.example.demo.entity.VolunteerEnrollment;
import com.example.demo.enums.EnrollmentStatus;
import com.example.demo.enums.OpportunityStatus;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.repository.VolunteerEnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final VolunteerEnrollmentRepository enrollmentRepository;

    private final SystemUserRepository userRepository;

    private final OpportunityRepository opportunityRepository;



    @Transactional
public EnrollmentResponseDto enrollVolunteer(
        Long volunteerId,
        EnrollmentRequestDto dto
) {

    SystemUser volunteer = userRepository.findById(volunteerId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Volunteer not found.")
            );

    if (volunteer.getRole() != Role.VOLUNTEER) {

        throw new BusinessValidationException(
                "Only users with VOLUNTEER role can enroll."
        );

    }

    if (volunteer.getStatus() != UserStatus.APPROVED) {

        throw new BusinessValidationException(
                "Your account must be APPROVED by an administrator before you can enroll."
        );

    }

    Opportunity opportunity =
            opportunityRepository.findById(dto.getOpportunityId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Opportunity not found."
                            )
                    );

    if (opportunity.getStatus() != OpportunityStatus.OPEN) {

        throw new BusinessValidationException(
                "Cannot enroll in opportunity. Only OPEN opportunities accept enrollments."
        );

    }

    if (enrollmentRepository.existsByVolunteerIdAndOpportunityId(
            volunteerId,
            dto.getOpportunityId())) {

        throw new BusinessValidationException(
                "You are already enrolled in this opportunity."
        );

    }

    if (opportunity.getEnrolledCount() >= opportunity.getMaxCapacity()) {

        throw new BusinessValidationException(
                "Opportunity is at full capacity."
        );

    }

    VolunteerEnrollment enrollment =
            VolunteerEnrollment.builder()
                    .volunteer(volunteer)
                    .opportunity(opportunity)
                    .status(EnrollmentStatus.PENDING)
                    .notes(dto.getNotes())
                    .hoursLogged(0.0)
                    .build();

    enrollmentRepository.save(enrollment);

    opportunity.setEnrolledCount(
            opportunity.getEnrolledCount() + 1
    );

    opportunityRepository.save(opportunity);

    return mapToResponseDto(enrollment);

    }

    






    private EnrollmentResponseDto mapToResponseDto(
        VolunteerEnrollment enrollment
    ) {

    return EnrollmentResponseDto.builder()
            .id(enrollment.getId())
            .volunteerId(enrollment.getVolunteer().getId())
            .volunteerName(enrollment.getVolunteer().getFullName())
            .opportunityId(enrollment.getOpportunity().getId())
            .opportunityTitle(enrollment.getOpportunity().getTitle())
            .organizationName(
                    enrollment.getOpportunity()
                            .getOrganization()
                            .getName()
            )
            .status(enrollment.getStatus().name())
            .enrolledAt(enrollment.getEnrolledAt())
            .approvedAt(enrollment.getApprovedAt())
            .hoursLogged(enrollment.getHoursLogged())
            .notes(enrollment.getNotes())
            .build();

    }



}