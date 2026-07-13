// package com.example.demo.controller;

// import com.example.demo.dto.dashboard.DashboardStatsDto;
// import com.example.demo.service.DashboardService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/dashboard")
// @RequiredArgsConstructor
// public class DashboardController {

//     private final DashboardService dashboardService;

//     @GetMapping("/stats")
//     @PreAuthorize("hasRole('PLATFORM_ADMIN')")
//     public ResponseEntity<DashboardStatsDto> getPlatformStats() {
//         return ResponseEntity.ok(dashboardService.getPlatformStats());
//     }
// }
