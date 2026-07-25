package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityRequestDto {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    @Size(max = 255)
    private String location;

    @Size(max = 50)
    private String category;

    @NotBlank(message = "Scheduled date is required")
    private String scheduledDate;

    @NotBlank(message = "Start time is required")
    private String startTime;

    @NotBlank(message = "End time is required")
    private String endTime;

    @NotNull
    @Min(1)
    private Integer maxCapacity;

    @Size(max = 500)
    private String requiredSkills;

    @NotNull
    private Long organizationId;

}