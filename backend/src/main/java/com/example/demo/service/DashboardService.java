package com.example.demo.service;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.enums.EnrollmentStatus;
import com.example.demo.enums.OpportunityStatus;
import com.example.demo.enums.OrganizationStatus;
import com.example.demo.enums.Role;
import com.example.demo.repository.ImpactReportRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.repository.VolunteerEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SystemUserRepository userRepository;

    private final OrganizationRepository organizationRepository;

    private final OpportunityRepository opportunityRepository;

    private final VolunteerEnrollmentRepository enrollmentRepository;

    private final ImpactReportRepository impactReportRepository;

    public DashboardStatsDto getDashboardStatistics() {

    return DashboardStatsDto.builder()

            .totalVolunteers(
                    userRepository.countByRole(Role.VOLUNTEER)
            )

            .totalOrganizations(
                    organizationRepository.count()
            )

            .totalOpportunities(
                    opportunityRepository.count()
            )

            .totalEnrollments(
                    enrollmentRepository.count()
            )

            .pendingOrganizations(
                    organizationRepository.countByStatus(
                            OrganizationStatus.PENDING
                    )
            )

            .approvedOrganizations(
                    organizationRepository.countByStatus(
                            OrganizationStatus.APPROVED
                    )
            )

            .openOpportunities(
                    opportunityRepository.countByStatus(
                            OpportunityStatus.OPEN
                    )
            )

            .pendingEnrollments(
                    enrollmentRepository.countByStatus(
                            EnrollmentStatus.PENDING
                    )
            )

            .approvedEnrollments(
                    enrollmentRepository.countByStatus(
                            EnrollmentStatus.APPROVED
                    )
            )

            .completedEnrollments(
                    enrollmentRepository.countByStatus(
                            EnrollmentStatus.COMPLETED
                    )
            )

            .rejectedEnrollments(
                    enrollmentRepository.countByStatus(
                            EnrollmentStatus.REJECTED
                    )
            )

            .totalImpactReports(
                    impactReportRepository.count()
            )

            .build();

    }

    

}