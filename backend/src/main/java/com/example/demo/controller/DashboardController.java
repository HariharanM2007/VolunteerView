package com.example.demo.controller;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
public ResponseEntity<DashboardStatsDto> getDashboardStatistics() {

    return ResponseEntity.ok(
            dashboardService.getDashboardStatistics()
    );

    }

}