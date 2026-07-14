package com.example.demo.service;

import com.example.demo.dto.impact.ImpactReportRequestDto;
import com.example.demo.dto.impact.ImpactReportResponseDto;
import com.example.demo.dto.impact.ImpactSummaryDto;
import com.example.demo.entity.ImpactReport;
import com.example.demo.entity.SystemUser;
import com.example.demo.entity.VolunteerEnrollment;
import com.example.demo.enums.EnrollmentStatus;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ImpactReportRepository;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.repository.VolunteerEnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpactReportService {

    private final ImpactReportRepository impactReportRepository;

    private final VolunteerEnrollmentRepository enrollmentRepository;

    private final SystemUserRepository userRepository;











    private ImpactReportResponseDto mapToResponseDto(
        ImpactReport report
) {

    return ImpactReportResponseDto.builder()
            .id(report.getId())
            .enrollmentId(report.getEnrollment().getId())
            .volunteerId(report.getVolunteer().getId())
            .volunteerName(report.getVolunteer().getFullName())
            .opportunityTitle(
                    report.getEnrollment()
                            .getOpportunity()
                            .getTitle()
            )
            .organizationName(
                    report.getEnrollment()
                            .getOpportunity()
                            .getOrganization()
                            .getName()
            )
            .summary(report.getSummary())
            .hoursContributed(report.getHoursContributed())
            .beneficiariesServed(report.getBeneficiariesServed())
            .rating(report.getRating())
            .submittedAt(report.getSubmittedAt())
            .build();

}

}