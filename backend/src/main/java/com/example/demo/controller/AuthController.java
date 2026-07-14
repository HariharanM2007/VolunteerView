package com.example.demo.controller;

import com.example.demo.dto.auth.AuthRequestDto;
import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.auth.RefreshTokenRequestDto;
import com.example.demo.dto.auth.RegisterDto;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(

        @Valid
        @RequestBody RegisterDto dto

    ) {

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.register(dto));

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(

        @Valid
        @RequestBody AuthRequestDto dto

    ) {

    return ResponseEntity.ok(
            authService.login(dto)
    );

    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(

        @RequestBody RefreshTokenRequestDto dto

    ) {

    return ResponseEntity.ok(
            authService.refreshToken(dto)
    );

    }

}