package com.example.demo.controller;

import com.example.demo.dto.OrganizationRequestDto;
import com.example.demo.dto.OrganizationResponseDto;
import com.example.demo.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;


    @PostMapping
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
    public ResponseEntity<OrganizationResponseDto> approveOrganization(

        @PathVariable Long id

    ) {

    return ResponseEntity.ok(
            organizationService.approveOrganization(id)
    );

    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<OrganizationResponseDto> suspendOrganization(

        @PathVariable Long id

    ) {

    return ResponseEntity.ok(
            organizationService.suspendOrganization(id)
    );

    }

    @GetMapping
    public ResponseEntity<Page<OrganizationResponseDto>> getAllOrganizations(

        Pageable pageable

    ) {

    return ResponseEntity.ok(
            organizationService.getAllOrganizations(pageable)
    );

    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponseDto> getOrganizationById(

        @PathVariable Long id

    ) {

    return ResponseEntity.ok(
            organizationService.getOrganizationById(id)
    );

    }

    @GetMapping("/coordinator/{coordinatorId}")
public ResponseEntity<List<OrganizationResponseDto>> getOrganizationsByCoordinator(

        @PathVariable Long coordinatorId

) {

    return ResponseEntity.ok(
            organizationService.getOrganizationsByCoordinator(coordinatorId)
    );

    }

    @DeleteMapping("/{id}")
public ResponseEntity<Void> deleteOrganization(

        @PathVariable Long id

) {

    organizationService.deleteOrganization(id);

    return ResponseEntity.noContent().build();

    }

    



}