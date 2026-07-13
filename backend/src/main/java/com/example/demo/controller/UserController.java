// package com.example.demo.controller;

// import com.example.demo.dto.user.UserResponseDto;
// import com.example.demo.service.UserService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/users")
// @PreAuthorize("hasRole('PLATFORM_ADMIN')")
// @RequiredArgsConstructor
// public class UserController {

//     private final UserService userService;

//     @GetMapping
//     public ResponseEntity<List<UserResponseDto>> getAllUsers() {
//         return ResponseEntity.ok(userService.getAllUsers());
//     }

//     @PatchMapping("/{id}/status")
//     public ResponseEntity<UserResponseDto> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
//         String status = payload.get("status");
//         return ResponseEntity.ok(userService.updateStatus(id, status));
//     }

//     @PatchMapping("/{id}/assign-organization/{orgId}")
//     public ResponseEntity<UserResponseDto> assignOrganization(@PathVariable Long id, @PathVariable Long orgId) {
//         return ResponseEntity.ok(userService.assignOrganization(id, orgId));
//     }
// }
