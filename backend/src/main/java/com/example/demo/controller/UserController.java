package com.example.demo.controller;

import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{id}/approve")
public ResponseEntity<UserResponseDto> approveUser(

        @PathVariable Long id

) {

    return ResponseEntity.ok(
            userService.approveUser(id)
    );

    }

    @PatchMapping("/{id}/deactivate")
public ResponseEntity<UserResponseDto> deactivateUser(

        @PathVariable Long id

) {

    return ResponseEntity.ok(
            userService.deactivateUser(id)
    );

    }

    @GetMapping
public ResponseEntity<Page<UserResponseDto>> getAllUsers(

        @ParameterObject Pageable pageable

) {

    return ResponseEntity.ok(
            userService.getAllUsers(pageable)
    );

    }

    @GetMapping("/{id}")
public ResponseEntity<UserResponseDto> getUserById(

        @PathVariable Long id

) {

    return ResponseEntity.ok(
            userService.getUserById(id)
    );

    }

    

}