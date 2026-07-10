package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityResponseDto {

    private Long id;
    private String title;
    private String description;
    private String location;
    private String category;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxCapacity;
    private Integer enrolledCount;
    private String requiredSkills;
    private String status;
    private Long organizationId;
    private String organizationName;
    private boolean isEnrolled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
