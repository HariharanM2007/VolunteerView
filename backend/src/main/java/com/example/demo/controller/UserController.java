package com.example.demo.controller;

import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/approve")
    public ResponseEntity<UserResponseDto> approveUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDto> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String status = body == null ? null : body.get("status");
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required.");
        }
        return ResponseEntity.ok(userService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/assign-organization/{orgId}")
    public ResponseEntity<UserResponseDto> assignOrganization(
            @PathVariable Long id,
            @PathVariable Long orgId
    ) {
        return ResponseEntity.ok(userService.assignOrganization(id, orgId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeactivatedUser(@PathVariable Long id) {
        userService.deleteDeactivatedUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
