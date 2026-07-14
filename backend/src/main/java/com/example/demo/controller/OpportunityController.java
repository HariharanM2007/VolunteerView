package com.example.demo.controller;

import com.example.demo.dto.opportunity.OpportunityRequestDto;
import com.example.demo.dto.opportunity.OpportunityResponseDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.service.OpportunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
public ResponseEntity<OpportunityResponseDto> createOpportunity(

        @Valid
        @RequestBody OpportunityRequestDto dto,

        @AuthenticationPrincipal
        SystemUser actor

) {

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                    opportunityService.createOpportunity(dto, actor)
            );

    }

     

}