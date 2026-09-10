package com.example.demo.controller;

import com.example.demo.dto.OrganizationRequestDto;
import com.example.demo.dto.OrganizationResponseDto;
import com.example.demo.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;


    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<OrganizationResponseDto> createOrganization(

        @Valid
        @RequestBody OrganizationRequestDto dto

    ) {

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                    organizationService.createOrganization(dto)
            );

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<OrganizationResponseDto> updateOrganization(

        @PathVariable Long id,

        @Valid
        @RequestBody OrganizationRequestDto dto

    ) {

    return ResponseEntity.ok(
            organizationService.updateOrganization(id, dto)
    );

    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<OrganizationResponseDto> approveOrganization(

        @PathVariable Long id

    ) {

    return ResponseEntity.ok(
            organizationService.approveOrganization(id)
    );

    }

    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<OrganizationResponseDto> suspendOrganization(

        @PathVariable Long id

    ) {

    return ResponseEntity.ok(
            organizationService.suspendOrganization(id)
    );

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR', 'VOLUNTEER')")
    public ResponseEntity<Page<OrganizationResponseDto>> getAllOrganizations(

        @ParameterObject Pageable pageable

    ) {

    return ResponseEntity.ok(
            organizationService.getAllOrganizations(pageable)
    );

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR', 'VOLUNTEER')")
    public ResponseEntity<OrganizationResponseDto> getOrganizationById(

        @PathVariable Long id

    ) {

    return ResponseEntity.ok(
            organizationService.getOrganizationById(id)
    );

    }

    @GetMapping("/coordinator/{coordinatorId}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
public ResponseEntity<List<OrganizationResponseDto>> getOrganizationsByCoordinator(

        @PathVariable Long coordinatorId

) {

    return ResponseEntity.ok(
            organizationService.getOrganizationsByCoordinator(coordinatorId)
    );

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
public ResponseEntity<Void> deleteOrganization(

        @PathVariable Long id

) {

    organizationService.deleteOrganization(id);

    return ResponseEntity.noContent().build();

    }

    



}