package com.example.demo.controller;

import com.example.demo.dto.EnrollmentRequestDto;
import com.example.demo.dto.EnrollmentResponseDto;
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
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<Page<EnrollmentResponseDto>> getMyEnrollments(
            @AuthenticationPrincipal Object principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (principal == null) {
            throw new BusinessValidationException("You must be logged in as a volunteer to view your enrollments.");
        }
        SystemUser user = (SystemUser) principal;
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByVolunteer(user.getId(), pageable));
    }

    @GetMapping("/organization/{orgId}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<Page<EnrollmentResponseDto>> getOrganizationEnrollments(
            @PathVariable Long orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByOrganization(orgId, pageable));
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<EnrollmentResponseDto> enroll(
            @AuthenticationPrincipal Object actor,
            @Valid @RequestBody EnrollmentRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enrollVolunteer(((SystemUser) actor).getId(), dto));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<EnrollmentResponseDto> approve(
            @AuthenticationPrincipal Object actor,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(enrollmentService.approveEnrollment(id, (SystemUser) actor));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<EnrollmentResponseDto> rejectEnrollment(
            @PathVariable Long id,
            @AuthenticationPrincipal SystemUser actor
    ) {
        return ResponseEntity.ok(enrollmentService.rejectEnrollment(id, actor));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<EnrollmentResponseDto> completeEnrollment(
            @PathVariable Long id,
            @RequestParam Double hours,
            @AuthenticationPrincipal SystemUser actor
    ) {
        return ResponseEntity.ok(enrollmentService.completeEnrollment(id, hours, actor));
    }

    @PatchMapping("/{id}/check-in")
    @PreAuthorize("hasAnyRole('VOLUNTEER', 'ORGANIZATION_COORDINATOR', 'PLATFORM_ADMIN')")
    public ResponseEntity<EnrollmentResponseDto> checkIn(
            @AuthenticationPrincipal Object actor,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(enrollmentService.checkIn(id, (SystemUser) actor));
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<Page<EnrollmentResponseDto>> getVolunteerEnrollments(
            @PathVariable Long volunteerId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByVolunteer(volunteerId, pageable));
    }
}
