package com.example.demo;

import com.example.demo.controller.UserController;
import com.example.demo.dto.RegisterDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class UserControllerTest {

    @Test
    public void approveUser_returnsApprovedUser() {
        UserService userService = Mockito.mock(UserService.class);
        UserController controller = new UserController(userService);

        UserResponseDto dto = UserResponseDto.builder()
                .id(1L)
                .username("demo")
                .email("demo@example.com")
                .fullName("Demo User")
                .role("PLATFORM_ADMIN")
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(userService.approveUser(1L)).thenReturn(dto);

        ResponseEntity<UserResponseDto> response = controller.approveUser(1L);

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody().getStatus(), "APPROVED");
    }

    @Test
    public void updateStatus_acceptsStatusBody() {
        UserService userService = Mockito.mock(UserService.class);
        UserController controller = new UserController(userService);

        UserResponseDto dto = UserResponseDto.builder()
                .id(2L)
                .username("volunteer")
                .email("volunteer@example.com")
                .fullName("Volunteer User")
                .role("VOLUNTEER")
                .status("DEACTIVATED")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(userService.updateStatus(2L, "DEACTIVATED")).thenReturn(dto);

        ResponseEntity<UserResponseDto> response = controller.updateStatus(2L, Map.of("status", "DEACTIVATED"));

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(response.getBody().getStatus(), "DEACTIVATED");
    }

    @Test
    public void userService_rejectsSelfDeactivationForCurrentAdmin() {
        SystemUserRepository userRepository = Mockito.mock(SystemUserRepository.class);
        OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);
        UserService userService = new UserService(userRepository, organizationRepository);

        SystemUser currentAdmin = SystemUser.builder()
                .id(7L)
                .username("admin")
                .email("admin@example.com")
                .fullName("Platform Admin")
                .role(Role.PLATFORM_ADMIN)
                .status(UserStatus.APPROVED)
                .isActive(true)
                .build();

        SystemUser targetUser = SystemUser.builder()
                .id(7L)
                .username("admin")
                .email("admin@example.com")
                .fullName("Platform Admin")
                .role(Role.PLATFORM_ADMIN)
                .status(UserStatus.APPROVED)
                .isActive(true)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentAdmin, null, currentAdmin.getAuthorities())
        );

        Mockito.when(userRepository.findById(7L)).thenReturn(Optional.of(targetUser));

        try {
            userService.deactivateUser(7L);
            Assert.fail("Expected self-deactivation to be rejected.");
        } catch (BusinessValidationException ex) {
            Assert.assertTrue(ex.getMessage().contains("deactivate your own"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    public void authService_rejectsPlatformAdminPublicRegistration() {
        SystemUserRepository userRepository = Mockito.mock(SystemUserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        AuthService authService = new AuthService(userRepository, passwordEncoder, Mockito.mock(com.example.demo.security.JwtService.class));

        Mockito.when(userRepository.existsByUsername("adminuser")).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("Secret123")).thenReturn("hashed");

        RegisterDto registerDto = RegisterDto.builder()
                .username("adminuser")
                .email("admin@example.com")
                .password("Secret123")
                .fullName("Admin User")
                .phoneNumber("1234567890")
                .role("PLATFORM_ADMIN")
                .build();

        try {
            authService.register(registerDto);
            Assert.fail("Expected registration as PLATFORM_ADMIN to be rejected.");
        } catch (BusinessValidationException ex) {
            Assert.assertTrue(ex.getMessage().toLowerCase().contains("public registration"));
        }
    }
}
