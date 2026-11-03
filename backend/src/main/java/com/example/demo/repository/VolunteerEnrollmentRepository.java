package com.example.demo.repository;

import com.example.demo.entity.VolunteerEnrollment;
import com.example.demo.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface VolunteerEnrollmentRepository
        extends JpaRepository<VolunteerEnrollment, Long> {

        @Modifying
        @Query("delete from VolunteerEnrollment ve where ve.opportunity.id = :opportunityId")
        void deleteByOpportunityId(@Param("opportunityId") Long opportunityId);

        @Modifying
        @Query("delete from VolunteerEnrollment ve where ve.volunteer.id = :volunteerId")
        void deleteByVolunteerId(@Param("volunteerId") Long volunteerId);

    boolean existsByVolunteerIdAndOpportunityId(
            Long volunteerId,
            Long opportunityId
    );

    @Query("""
            SELECT ve
            FROM VolunteerEnrollment ve
            JOIN FETCH ve.volunteer
            JOIN FETCH ve.opportunity
            JOIN FETCH ve.opportunity.organization
            WHERE ve.id = :id
            """)
    Optional<VolunteerEnrollment> findWithDetailsById(
            @Param("id") Long id
    );

    List<VolunteerEnrollment> findByVolunteerId(Long volunteerId);

    List<VolunteerEnrollment> findByOpportunityId(Long opportunityId);

    @EntityGraph(attributePaths = {
            "volunteer",
            "opportunity",
            "opportunity.organization"
    })
    Page<VolunteerEnrollment> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {
            "volunteer",
            "opportunity",
            "opportunity.organization"
    })
    Page<VolunteerEnrollment> findByVolunteerId(
            Long volunteerId,
            Pageable pageable
    );

    List<VolunteerEnrollment> findByStatus(
            EnrollmentStatus status
    );

    long countByStatus(
            EnrollmentStatus status
    );

    @Query("""
            SELECT ve
            FROM VolunteerEnrollment ve
            WHERE ve.opportunity.id = :opportunityId
            AND ve.status = :status
            """)
    List<VolunteerEnrollment> findByOpportunityIdAndStatus(
            @Param("opportunityId") Long opportunityId,
            @Param("status") EnrollmentStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(ve.hoursLogged),0)
            FROM VolunteerEnrollment ve
            WHERE ve.volunteer.id = :volunteerId
            """)
    Optional<Double> sumHoursLoggedByVolunteerId(
            @Param("volunteerId") Long volunteerId
    );

    @Query("""
            SELECT COUNT(ve)
            FROM VolunteerEnrollment ve
            WHERE ve.opportunity.organization.id = :orgId
            """)
    long countByOrganizationId(
            @Param("orgId") Long orgId
    );

    @Query("""
            SELECT ve
            FROM VolunteerEnrollment ve
            WHERE ve.opportunity.organization.id = :orgId
            """)
    @EntityGraph(attributePaths = {
            "volunteer",
            "opportunity",
            "opportunity.organization"
    })
    Page<VolunteerEnrollment> findByOrganizationId(
            @Param("orgId") Long orgId,
            Pageable pageable
    );

}