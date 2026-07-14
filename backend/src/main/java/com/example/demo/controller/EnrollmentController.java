package com.example.demo.controller;

import com.example.demo.dto.enrollment.EnrollmentRequestDto;
import com.example.demo.dto.enrollment.EnrollmentResponseDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
public ResponseEntity<EnrollmentResponseDto> enrollVolunteer(

        @AuthenticationPrincipal
        SystemUser actor,

        @Valid
        @RequestBody EnrollmentRequestDto dto

) {

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                    enrollmentService.enrollVolunteer(
                            actor.getId(),
                            dto
                    )
            );

    }

    @PatchMapping("/{id}/approve")
public ResponseEntity<EnrollmentResponseDto> approveEnrollment(

        @PathVariable Long id,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            enrollmentService.approveEnrollment(id, actor)
    );

    }

    @PatchMapping("/{id}/reject")
public ResponseEntity<EnrollmentResponseDto> rejectEnrollment(

        @PathVariable Long id,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            enrollmentService.rejectEnrollment(id, actor)
    );

    }

    @PatchMapping("/{id}/check-in")
public ResponseEntity<EnrollmentResponseDto> checkIn(

        @PathVariable Long id,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            enrollmentService.checkIn(id, actor)
    );

    }

    @PatchMapping("/{id}/complete")
public ResponseEntity<EnrollmentResponseDto> completeEnrollment(

        @PathVariable Long id,

        @RequestParam Double hours,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            enrollmentService.completeEnrollment(
                    id,
                    hours,
                    actor
            )
    );

    }

    @GetMapping
public ResponseEntity<Page<EnrollmentResponseDto>> getAllEnrollments(

        Pageable pageable

) {

    return ResponseEntity.ok(
            enrollmentService.getAllEnrollments(pageable)
    );

    }

    @GetMapping("/volunteer/{volunteerId}")
public ResponseEntity<Page<EnrollmentResponseDto>> getVolunteerEnrollments(

        @PathVariable Long volunteerId,

        Pageable pageable

) {

    return ResponseEntity.ok(
            enrollmentService.getEnrollmentsByVolunteer(
                    volunteerId,
                    pageable
            )
    );

    }

    @GetMapping("/organization/{organizationId}")
public ResponseEntity<Page<EnrollmentResponseDto>> getOrganizationEnrollments(

        @PathVariable Long organizationId,

        Pageable pageable

) {

    return ResponseEntity.ok(
            enrollmentService.getEnrollmentsByOrganization(
                    organizationId,
                    pageable
            )
    );

    }

    

}