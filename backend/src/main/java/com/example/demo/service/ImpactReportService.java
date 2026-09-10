package com.example.demo.service;

import com.example.demo.dto.ImpactReportRequestDto;
import com.example.demo.dto.ImpactReportResponseDto;
import com.example.demo.dto.ImpactSummaryDto;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpactReportService {

    private final ImpactReportRepository impactReportRepository;

    private final VolunteerEnrollmentRepository enrollmentRepository;

    private final SystemUserRepository userRepository;

    @Transactional
public ImpactReportResponseDto submitImpactReport(
        Long volunteerId,
        ImpactReportRequestDto dto
) {

    SystemUser volunteer = userRepository.findById(volunteerId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Volunteer not found.")
            );

    VolunteerEnrollment enrollment =
            enrollmentRepository.findWithDetailsById(dto.getEnrollmentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Enrollment not found."
                            )
                    );

    if (!enrollment.getVolunteer().getId().equals(volunteerId)) {

        throw new BusinessValidationException(
                "You can only submit reports for your own enrollments."
        );

    }

    if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {

        throw new BusinessValidationException(
                "Impact reports can only be submitted for COMPLETED enrollments."
        );

    }

    if (impactReportRepository.existsByEnrollmentId(
            enrollment.getId())) {

        throw new BusinessValidationException(
                "An impact report has already been submitted."
        );

    }

    if (dto.getHoursContributed() > enrollment.getHoursLogged()) {

        throw new BusinessValidationException(
                "Hours contributed cannot exceed logged hours."
        );

    }

    ImpactReport report = ImpactReport.builder()
            .enrollment(enrollment)
            .volunteer(volunteer)
            .summary(dto.getSummary())
            .hoursContributed(dto.getHoursContributed())
            .beneficiariesServed(dto.getBeneficiariesServed())
            .rating(dto.getRating())
            .build();

    impactReportRepository.save(report);

    return mapToResponseDto(report);

    }


    @Transactional
public Page<ImpactReportResponseDto> getAllReports(Pageable pageable) {
    return impactReportRepository.findAll(pageable).map(this::mapToResponseDto);
    }

    @Transactional
public Page<ImpactReportResponseDto> getReportsByVolunteerPaged(Long volunteerId, Pageable pageable) {
    return impactReportRepository.findByVolunteerId(volunteerId, pageable).map(this::mapToResponseDto);
    }

    @Transactional
public ImpactSummaryDto getOrganizationImpactSummary(Long orgId) {
    Double totalHours = impactReportRepository.sumHoursContributedByOrganizationId(orgId);
    Integer totalBeneficiaries = impactReportRepository.sumBeneficiariesServedByOrganizationId(orgId);
    long totalReports = impactReportRepository.countByOrganizationId(orgId);
    return ImpactSummaryDto.builder()
            .totalHoursContributed(totalHours)
            .totalBeneficiariesServed(totalBeneficiaries)
            .averageRating(0.0)
            .totalReports(totalReports)
            .build();
    }

    @Transactional
public List<ImpactReportResponseDto> getReportsByVolunteer(
        Long volunteerId
) {

    return impactReportRepository.findByVolunteerId(volunteerId)
            .stream()
            .map(this::mapToResponseDto)
            .toList();

    }

    @Transactional
public List<ImpactReportResponseDto> getReportsByOrganization(
        Long organizationId
) {

    return impactReportRepository.findByOrganizationId(organizationId)
            .stream()
            .map(this::mapToResponseDto)
            .toList();

    }

    @Transactional
public ImpactSummaryDto getImpactSummary(
        Long volunteerId
) {

    Double totalHours =
            impactReportRepository
                    .sumHoursContributedByVolunteerId(volunteerId);

    Integer totalBeneficiaries =
            impactReportRepository
                    .sumBeneficiariesServedByVolunteerId(volunteerId);

    Double averageRating =
            impactReportRepository
                    .avgRatingByVolunteerId(volunteerId);

    Long totalReports =
            (long) impactReportRepository
                    .findByVolunteerId(volunteerId)
                    .size();

    return ImpactSummaryDto.builder()
            .totalHoursContributed(totalHours)
            .totalBeneficiariesServed(totalBeneficiaries)
            .averageRating(averageRating)
            .totalReports(totalReports)
            .build();

    }

    











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