package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ImpactReportRequestDto {

    @NotNull
    private Long enrollmentId;

    @NotBlank
    private String summary;

    @NotNull
    @DecimalMin("0.5")
    private Double hoursContributed;

    @NotNull
    @Min(0)
    private Integer beneficiariesServed;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
}
