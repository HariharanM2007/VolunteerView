package com.example.demo.repository;

import com.example.demo.entity.Organization;
import com.example.demo.enums.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsByName(String name);

    /*
     * Kept unchanged so existing code/tests that use this method
     * are not affected.
     */
    Optional<Organization> findByCoordinatorId(Long coordinatorId);

    /*
     * Used when a coordinator can have multiple organizations.
     * This matches the @ManyToOne relationship between
     * Organization and SystemUser.
     */
    Optional<Organization> findFirstByCoordinatorIdOrderByIdAsc(Long coordinatorId);

    List<Organization> findByStatus(OrganizationStatus status);

    Page<Organization> findAll(Pageable pageable);

    long countByStatus(OrganizationStatus status);

    @Query("""
            SELECT o
            FROM Organization o
            WHERE o.coordinator.id = :coordinatorId
            """)
    List<Organization> findAllByCoordinatorId(
            @Param("coordinatorId") Long coordinatorId
    );
}