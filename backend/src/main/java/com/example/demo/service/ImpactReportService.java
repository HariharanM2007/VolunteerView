package com.example.demo.service;

import com.example.demo.dto.ImpactSummaryDto;
import com.example.demo.dto.impact.ImpactReportRequestDto;
import com.example.demo.dto.impact.ImpactReportResponseDto;
import com.example.demo.entity.ImpactReport;
import com.example.demo.entity.VolunteerEnrollment;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ImpactReportRepository;
import com.example.demo.repository.VolunteerEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpactReportService {

    private final ImpactReportRepository impactReportRepository;
    private final VolunteerEnrollmentRepository enrollmentRepository;

    public ImpactReportResponseDto submitReport(Long volunteerId, ImpactReportRequestDto dto) {
        VolunteerEnrollment enrollment = enrollmentRepository.findById(dto.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("VolunteerEnrollment", dto.getEnrollmentId()));

        if (!enrollment.getVolunteer().getId().equals(volunteerId)) {
            throw new BusinessValidationException("You can only submit impact reports for your own enrollments");
        }

        if (enrollment.getStatus() != VolunteerEnrollment.EnrollmentStatus.COMPLETED) {
            throw new BusinessValidationException("Impact reports can only be submitted for COMPLETED enrollments. Current status: " + enrollment.getStatus());
        }

        if (impactReportRepository.existsByEnrollmentId(dto.getEnrollmentId())) {
            throw new BusinessValidationException("An impact report has already been submitted for this enrollment");
        }

        if (dto.getHoursContributed() > enrollment.getHoursLogged() * 1.5) {
            throw new BusinessValidationException("Reported hours (" + dto.getHoursContributed() + ") significantly exceed logged hours (" + enrollment.getHoursLogged() + "). Please verify your hours.");
        }

        ImpactReport report = ImpactReport.builder()
                .enrollment(enrollment)
                .volunteer(enrollment.getVolunteer())
                .summary(dto.getSummary())
                .hoursContributed(dto.getHoursContributed())
                .beneficiariesServed(dto.getBeneficiariesServed())
                .rating(dto.getRating())
                .build();

        ImpactReport saved = impactReportRepository.save(report);
        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public ImpactSummaryDto getVolunteerImpactSummary(Long volunteerId) {
        Double totalHours = impactReportRepository.sumHoursContributedByVolunteerId(volunteerId);
        Integer totalBeneficiaries = impactReportRepository.sumBeneficiariesServedByVolunteerId(volunteerId);
        Double averageRating = impactReportRepository.avgRatingByVolunteerId(volunteerId);
        long totalReports = impactReportRepository.findByVolunteerId(volunteerId).size();

        return ImpactSummaryDto.builder()
                .totalHoursContributed(totalHours)
                .totalBeneficiariesServed(totalBeneficiaries)
                .averageRating(averageRating)
                .totalReports(totalReports)
                .build();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Page<ImpactReportResponseDto> getAllReports(Pageable pageable) {
        return impactReportRepository.findAll(pageable).map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ImpactReportResponseDto> getReportsByVolunteer(Long volunteerId, Pageable pageable) {
        return impactReportRepository.findByVolunteerId(volunteerId, pageable).map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public List<ImpactReportResponseDto> getReportsByOrganization(Long orgId) {
        return impactReportRepository.findByOrganizationId(orgId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ImpactReportResponseDto getReportById(Long id) {
        ImpactReport report = impactReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ImpactReport", id));
        return mapToResponseDto(report);
    }

    private ImpactReportResponseDto mapToResponseDto(ImpactReport report) {
        return ImpactReportResponseDto.builder()
                .id(report.getId())
                .enrollmentId(report.getEnrollment() != null ? report.getEnrollment().getId() : null)
                .volunteerId(report.getVolunteer() != null ? report.getVolunteer().getId() : null)
                .volunteerName(report.getVolunteer() != null ? report.getVolunteer().getFullName() : null)
                .opportunityTitle(report.getEnrollment() != null && report.getEnrollment().getOpportunity() != null
                        ? report.getEnrollment().getOpportunity().getTitle() : null)
                .organizationName(report.getEnrollment() != null
                        && report.getEnrollment().getOpportunity() != null
                        && report.getEnrollment().getOpportunity().getOrganization() != null
                        ? report.getEnrollment().getOpportunity().getOrganization().getName() : null)
                .summary(report.getSummary())
                .hoursContributed(report.getHoursContributed())
                .beneficiariesServed(report.getBeneficiariesServed())
                .rating(report.getRating())
                .submittedAt(report.getSubmittedAt())
                .build();
    }
}
