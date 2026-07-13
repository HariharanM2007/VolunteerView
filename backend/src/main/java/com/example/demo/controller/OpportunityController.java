package com.example.demo.controller;

import com.example.demo.dto.opportunity.OpportunityRequestDto;
import com.example.demo.dto.opportunity.OpportunityResponseDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.service.OpportunityService;
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
@RequestMapping("/api/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final SystemUserRepository systemUserRepository;

    @GetMapping
    public ResponseEntity<Page<OpportunityResponseDto>> getAllOpportunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(opportunityService.getAllOpportunities(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponseDto> getOpportunityById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getOpportunityById(id));
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<OpportunityResponseDto>> getOpportunitiesByOrganization(@PathVariable Long orgId) {
        return ResponseEntity.ok(opportunityService.getOpportunitiesByOrganization(orgId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<OpportunityResponseDto> createOpportunity(@AuthenticationPrincipal Object principal,
                                                                     @Valid @RequestBody OpportunityRequestDto dto) {
        SystemUser actor = getAttachedUser(principal);
        OpportunityResponseDto response = opportunityService.createOpportunity(dto, actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<OpportunityResponseDto> updateOpportunity(@AuthenticationPrincipal Object principal,
                                                                     @PathVariable Long id,
                                                                     @Valid @RequestBody OpportunityRequestDto dto) {
        SystemUser actor = getAttachedUser(principal);
        return ResponseEntity.ok(opportunityService.updateOpportunity(id, dto, actor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<Void> deleteOpportunity(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        SystemUser actor = getAttachedUser(principal);
        opportunityService.deleteOpportunity(id, actor);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<OpportunityResponseDto> publishOpportunity(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        SystemUser actor = getAttachedUser(principal);
        return ResponseEntity.ok(opportunityService.publishOpportunity(id, actor));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<OpportunityResponseDto> closeOpportunity(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        SystemUser actor = getAttachedUser(principal);
        return ResponseEntity.ok(opportunityService.closeOpportunity(id, actor));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'ORGANIZATION_COORDINATOR')")
    public ResponseEntity<OpportunityResponseDto> completeOpportunity(@AuthenticationPrincipal Object principal, @PathVariable Long id) {
        SystemUser actor = getAttachedUser(principal);
        return ResponseEntity.ok(opportunityService.completeOpportunity(id, actor));
    }

    /**
     * Casts the authentication principal to SystemUser and reloads it via the repository
     * to guarantee an attached JPA entity (avoids detached-entity issues in service layer).
     */
    private SystemUser getAttachedUser(Object principal) {
        if (principal == null) {
            throw new BusinessValidationException("Authentication required to create opportunities");
        }
        SystemUser principalUser = (SystemUser) principal;
        return systemUserRepository.findById(principalUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("SystemUser", principalUser.getId()));
    }
}
