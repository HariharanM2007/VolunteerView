package com.example.demo.entity;

import com.example.demo.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "volunteer_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_volunteer_opportunity",
                        columnNames = {"volunteer_id", "opportunity_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private SystemUser volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status", nullable = false, length = 30)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Builder.Default
    @Column(name = "hours_logged")
    private Double hoursLogged = 0.0;

    @Column(length = 500)
    private String notes;

    @PrePersist
    public void onCreate() {

        enrolledAt = LocalDateTime.now();

        if (status == null) {
            status = EnrollmentStatus.PENDING;
        }

        if (hoursLogged == null) {
            hoursLogged = 0.0;
        }
    }

}