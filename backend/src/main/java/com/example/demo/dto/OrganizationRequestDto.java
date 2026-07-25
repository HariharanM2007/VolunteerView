package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationRequestDto {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    private String mission;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Email
    private String contactEmail;

    @Size(max = 20)
    private String contactPhone;

    @Min(1800)
    @Max(2100)
    private Integer foundedYear;

    private Long coordinatorId;

}