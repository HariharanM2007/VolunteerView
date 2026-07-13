package com.example.demo.controller;

import com.example.demo.dto.enrollment.EnrollmentRequestDto;
import com.example.demo.dto.enrollment.EnrollmentResponseDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.service.EnrollmentService;
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

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Page<EnrollmentResponseDto>> getAllEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "enrolledAt"));
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<Page<EnrollmentResponseDto>> getMyEnrollments(
            @AuthenticationPrincipal Object principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (principal == null) {
            throw new BusinessValidationException("You must be logged in as a volunteer to view your enrollments.");
        }
        SystemUser volunteer = (SystemUser) principal;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "enrolledAt"));
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByVolunteer(volunteer.getId(), pageable));
    }

    @GetMapping("/organization/{orgId}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<Page<EnrollmentResponseDto>> getEnrollmentsByOrganization(
            @PathVariable Long orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "enrolledAt"));
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByOrganization(orgId, pageable));
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<EnrollmentResponseDto> enrollVolunteer(@AuthenticationPrincipal Object principal,
                                                                  @Valid @RequestBody EnrollmentRequestDto dto) {
        SystemUser volunteer = (SystemUser) principal;
        EnrollmentResponseDto response = enrollmentService.enrollVolunteer(volunteer.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<EnrollmentResponseDto> approveEnrollment(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        SystemUser actor = (SystemUser) principal;
        return ResponseEntity.ok(enrollmentService.approveEnrollment(id, actor));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<EnrollmentResponseDto> rejectEnrollment(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        SystemUser actor = (SystemUser) principal;
        return ResponseEntity.ok(enrollmentService.rejectEnrollment(id, actor));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<EnrollmentResponseDto> completeEnrollment(@AuthenticationPrincipal Object principal,
                                                                      @PathVariable Long id,
                                                                      @RequestParam Double hours) {
        SystemUser actor = (SystemUser) principal;
        return ResponseEntity.ok(enrollmentService.completeEnrollment(id, hours, actor));
    }

    @PatchMapping("/{id}/check-in")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZATION_COORDINATOR', 'PLATFORM_ADMIN')")
    public ResponseEntity<EnrollmentResponseDto> checkin(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        SystemUser actor = (SystemUser) principal;
        return ResponseEntity.ok(enrollmentService.checkin(id, actor));
    }
}
