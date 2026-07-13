package com.example.demo.service;

import com.example.demo.dto.auth.AuthRequestDto;
import com.example.demo.dto.auth.AuthResponseDto;
import com.example.demo.dto.auth.RefreshTokenRequestDto;
import com.example.demo.dto.auth.RegisterDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SystemUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

}