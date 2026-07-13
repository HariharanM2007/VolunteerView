// package com.example.demo.controller;

// import com.example.demo.dto.impact.ImpactReportRequestDto;
// import com.example.demo.dto.impact.ImpactReportResponseDto;
// import com.example.demo.dto.impact.ImpactSummaryDto;
// import com.example.demo.entity.SystemUser;
// import com.example.demo.service.ImpactReportService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/impact-reports")
// @RequiredArgsConstructor
// public class ImpactReportController {

//     private final ImpactReportService impactReportService;

//     @GetMapping
//     @PreAuthorize("hasRole('PLATFORM_ADMIN')")
//     public ResponseEntity<Page<ImpactReportResponseDto>> getAllReports(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
//         Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
//         return ResponseEntity.ok(impactReportService.getAllReports(pageable));
//     }

//     @GetMapping("/my")
//     @PreAuthorize("hasRole('VOLUNTEER')")
//     public ResponseEntity<Page<ImpactReportResponseDto>> getMyReports(
//             @AuthenticationPrincipal Object principal,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
//         SystemUser volunteer = (SystemUser) principal;
//         Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
//         return ResponseEntity.ok(impactReportService.getReportsByVolunteer(volunteer.getId(), pageable));
//     }

//     @GetMapping("/organization/{orgId}")
//     @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
//     public ResponseEntity<List<ImpactReportResponseDto>> getReportsByOrganization(@PathVariable Long orgId) {
//         return ResponseEntity.ok(impactReportService.getReportsByOrganization(orgId));
//     }

//     @PostMapping
//     @PreAuthorize("hasRole('VOLUNTEER')")
//     public ResponseEntity<ImpactReportResponseDto> submitReport(@AuthenticationPrincipal Object principal,
//                                                                  @Valid @RequestBody ImpactReportRequestDto dto) {
//         SystemUser volunteer = (SystemUser) principal;
//         ImpactReportResponseDto response = impactReportService.submitReport(volunteer.getId(), dto);
//         return ResponseEntity.status(HttpStatus.CREATED).body(response);
//     }

//     @GetMapping("/volunteer/{volunteerId}/summary")
//     @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'VOLUNTEER')")
//     public ResponseEntity<ImpactSummaryDto> getVolunteerImpactSummary(@PathVariable Long volunteerId) {
//         return ResponseEntity.ok(impactReportService.getVolunteerImpactSummary(volunteerId));
//     }

//     @GetMapping("/organization/{orgId}/summary")
//     @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
//     public ResponseEntity<ImpactSummaryDto> getOrganizationImpactSummary(@PathVariable Long orgId) {
//         return ResponseEntity.ok(impactReportService.getOrganizationImpactSummary(orgId));
//     }
// }
