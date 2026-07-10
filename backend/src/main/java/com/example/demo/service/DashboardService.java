package com.example.demo.service;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.entity.VolunteerEnrollment;
import com.example.demo.repository.ImpactReportRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.repository.VolunteerEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SystemUserRepository systemUserRepository;
    private final OrganizationRepository organizationRepository;
    private final OpportunityRepository opportunityRepository;
    private final VolunteerEnrollmentRepository enrollmentRepository;
    private final ImpactReportRepository impactReportRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDto getPlatformStats() {
        return DashboardStatsDto.builder()
                .totalVolunteers(systemUserRepository.countByRole(SystemUser.Role.VOLUNTEER))
                .totalOrganizations(organizationRepository.count())
                .totalOpportunities(opportunityRepository.count())
                .totalEnrollments(enrollmentRepository.count())
                .pendingOrganizations(organizationRepository.countByStatus(Organization.OrganizationStatus.PENDING))
                .approvedOrganizations(organizationRepository.countByStatus(Organization.OrganizationStatus.APPROVED))
                .openOpportunities(opportunityRepository.countByStatus(Opportunity.OpportunityStatus.OPEN))
                .pendingEnrollments(enrollmentRepository.countByStatus(VolunteerEnrollment.EnrollmentStatus.PENDING))
                .approvedEnrollments(enrollmentRepository.countByStatus(VolunteerEnrollment.EnrollmentStatus.APPROVED))
                .completedEnrollments(enrollmentRepository.countByStatus(VolunteerEnrollment.EnrollmentStatus.COMPLETED))
                .rejectedEnrollments(enrollmentRepository.countByStatus(VolunteerEnrollment.EnrollmentStatus.REJECTED))
                .totalImpactReports(impactReportRepository.count())
                .build();
    }
}
