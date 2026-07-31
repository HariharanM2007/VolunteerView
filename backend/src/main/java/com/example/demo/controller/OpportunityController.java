package com.example.demo.controller;

import com.example.demo.dto.OpportunityRequestDto;
import com.example.demo.dto.OpportunityResponseDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.service.OpportunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final SystemUserRepository systemUserRepository;

    @PostMapping
    public ResponseEntity<OpportunityResponseDto> create(

        @AuthenticationPrincipal
        Object actor,

        @Valid
        @RequestBody OpportunityRequestDto dto

) {

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                    opportunityService.createOpportunity(dto, (SystemUser) actor)
            );

    }

     @PutMapping("/{id}")
public ResponseEntity<OpportunityResponseDto> updateOpportunity(

        @PathVariable Long id,

        @Valid
        @RequestBody OpportunityRequestDto dto,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            opportunityService.updateOpportunity(id, dto, actor)
    );

    }

    @PatchMapping("/{id}/publish")
public ResponseEntity<OpportunityResponseDto> publishOpportunity(

        @PathVariable Long id,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            opportunityService.publishOpportunity(id, actor)
    );

    }

    @PatchMapping("/{id}/close")
public ResponseEntity<OpportunityResponseDto> closeOpportunity(

        @PathVariable Long id,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            opportunityService.closeOpportunity(id, actor)
    );

    }

    @PatchMapping("/{id}/complete")
public ResponseEntity<OpportunityResponseDto> completeOpportunity(

        @PathVariable Long id,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.ok(
            opportunityService.completeOpportunity(id, actor)
    );

    }

    @DeleteMapping("/{id}")
public ResponseEntity<Void> deleteOpportunity(

        @PathVariable Long id,

        @AuthenticationPrincipal
        SystemUser actor

) {

    opportunityService.deleteOpportunity(id, actor);

    return ResponseEntity.noContent().build();

    }

    @GetMapping
public ResponseEntity<Page<OpportunityResponseDto>> getAllOpportunities(

        @ParameterObject Pageable pageable

) {

    return ResponseEntity.ok(
            opportunityService.getAllOpportunities(pageable)
    );

    }

    @GetMapping("/{id}")
public ResponseEntity<OpportunityResponseDto> getOpportunityById(

        @PathVariable Long id

) {

    return ResponseEntity.ok(
            opportunityService.getOpportunityById(id)
    );

    }

    @GetMapping("/organization/{organizationId}")
public ResponseEntity<List<OpportunityResponseDto>> getOpportunitiesByOrganization(

        @PathVariable Long organizationId

) {

    return ResponseEntity.ok(
            opportunityService.getOpportunitiesByOrganization(organizationId)
    );

    }

    

}