package com.example.demo.controller;

import com.example.demo.dto.ImpactReportRequestDto;
import com.example.demo.dto.ImpactReportResponseDto;
import com.example.demo.dto.ImpactSummaryDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.service.ImpactReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impact-reports")
@RequiredArgsConstructor
public class ImpactReportController {

    private final ImpactReportService impactReportService;

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Page<ImpactReportResponseDto>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        return ResponseEntity.ok(impactReportService.getAllReports(pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<Page<ImpactReportResponseDto>> getMyReports(
            @AuthenticationPrincipal SystemUser actor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        return ResponseEntity.ok(impactReportService.getReportsByVolunteerPaged(actor.getId(), pageable));
    }

    @GetMapping("/organization/{orgId}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<List<ImpactReportResponseDto>> getReportsByOrganization(
            @PathVariable Long orgId
    ) {
        return ResponseEntity.ok(impactReportService.getReportsByOrganization(orgId));
    }

    @PostMapping
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<ImpactReportResponseDto> submitReport(
            @AuthenticationPrincipal SystemUser actor,
            @Valid @RequestBody ImpactReportRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(impactReportService.submitImpactReport(actor.getId(), dto));
    }

    @GetMapping("/volunteer/{volunteerId}/summary")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ImpactSummaryDto> getVolunteerSummary(@PathVariable Long volunteerId) {
        return ResponseEntity.ok(impactReportService.getImpactSummary(volunteerId));
    }

    @GetMapping("/organization/{orgId}/summary")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<ImpactSummaryDto> getOrganizationSummary(@PathVariable Long orgId) {
        return ResponseEntity.ok(impactReportService.getOrganizationImpactSummary(orgId));
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<ImpactReportResponseDto>> getReportsByVolunteer(
            @PathVariable Long volunteerId
    ) {
        return ResponseEntity.ok(impactReportService.getReportsByVolunteer(volunteerId));
    }
}
