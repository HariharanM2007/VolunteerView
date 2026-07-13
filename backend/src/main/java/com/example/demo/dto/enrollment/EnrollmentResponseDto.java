package com.example.demo.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponseDto {

    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private Long opportunityId;
    private String opportunityTitle;
    private String organizationName;
    private String status;
    private LocalDateTime enrolledAt;
    private LocalDateTime approvedAt;
    private Double hoursLogged;
    private String notes;
}
