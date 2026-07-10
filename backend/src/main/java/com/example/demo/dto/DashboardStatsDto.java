package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {

    private long totalVolunteers;
    private long totalOrganizations;
    private long totalOpportunities;
    private long totalEnrollments;
    private long pendingOrganizations;
    private long approvedOrganizations;
    private long openOpportunities;
    private long pendingEnrollments;
    private long approvedEnrollments;
    private long completedEnrollments;
    private long rejectedEnrollments;
    private long totalImpactReports;
}
