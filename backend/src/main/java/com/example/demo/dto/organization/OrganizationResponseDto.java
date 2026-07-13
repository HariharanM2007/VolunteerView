package com.example.demo.dto.organization;

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
public class OrganizationResponseDto {

    private Long id;
    private String name;
    private String mission;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private Integer foundedYear;
    private String status;
    private Long coordinatorId;
    private String coordinatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
