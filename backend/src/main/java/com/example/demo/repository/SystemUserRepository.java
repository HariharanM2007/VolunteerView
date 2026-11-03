package com.example.demo.repository;

import com.example.demo.entity.SystemUser;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    Optional<SystemUser> findByUsername(String username);

    Optional<SystemUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<SystemUser> findByRole(Role role);

    List<SystemUser> findByStatus(UserStatus status);

    List<SystemUser> findByIsActiveTrue();

    List<SystemUser> findByOrganizationId(Long organizationId);

    long countByRole(Role role);

}