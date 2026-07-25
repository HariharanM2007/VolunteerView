package com.example.demo.controller;

import com.example.demo.dto.ImpactReportRequestDto;
import com.example.demo.dto.ImpactReportResponseDto;
import com.example.demo.dto.ImpactSummaryDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.service.ImpactReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impact-reports")
@RequiredArgsConstructor
public class ImpactReportController {

    private final ImpactReportService impactReportService;

    @PostMapping
public ResponseEntity<ImpactReportResponseDto> submitReport(

        @AuthenticationPrincipal
        SystemUser actor,

        @Valid
        @RequestBody ImpactReportRequestDto dto

) {

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                    impactReportService.submitImpactReport(
                            actor.getId(),
                            dto
                    )
            );

    }

    @GetMapping("/volunteer/{volunteerId}")
public ResponseEntity<List<ImpactReportResponseDto>> getReportsByVolunteer(

        @PathVariable Long volunteerId

) {

    return ResponseEntity.ok(
            impactReportService.getReportsByVolunteer(volunteerId)
    );

    }

    @GetMapping("/organization/{organizationId}")
public ResponseEntity<List<ImpactReportResponseDto>> getReportsByOrganization(

        @PathVariable Long organizationId

) {

    return ResponseEntity.ok(
            impactReportService.getReportsByOrganization(organizationId)
    );

    }

    @GetMapping("/summary/{volunteerId}")
public ResponseEntity<ImpactSummaryDto> getImpactSummary(

        @PathVariable Long volunteerId

) {

    return ResponseEntity.ok(
            impactReportService.getImpactSummary(volunteerId)
    );

    }

    

}