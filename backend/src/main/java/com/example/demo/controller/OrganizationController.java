package com.example.demo.controller;

import com.example.demo.dto.organization.OrganizationRequestDto;
import com.example.demo.dto.organization.OrganizationResponseDto;
import com.example.demo.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    

}