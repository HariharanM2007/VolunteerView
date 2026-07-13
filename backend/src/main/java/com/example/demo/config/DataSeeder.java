// package com.example.demo.config;

// import com.example.demo.entity.Opportunity;
// import com.example.demo.entity.Organization;
// import com.example.demo.entity.SystemUser;
// import com.example.demo.entity.VolunteerEnrollment;
// import com.example.demo.repository.OpportunityRepository;
// import com.example.demo.repository.OrganizationRepository;
// import com.example.demo.repository.SystemUserRepository;
// import com.example.demo.repository.VolunteerEnrollmentRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.List;

// @Component
// @RequiredArgsConstructor
// public class DataSeeder implements CommandLineRunner {

//     private final SystemUserRepository systemUserRepository;
//     private final OrganizationRepository organizationRepository;
//     private final OpportunityRepository opportunityRepository;
//     private final VolunteerEnrollmentRepository enrollmentRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Override
//     public void run(String... args) {
//         if (systemUserRepository.count() != 0) {
//             return;
//         }

//         // Platform admin
//         SystemUser admin = SystemUser.builder()
//                 .username("admin")
//                 .email("admin@volunteerview.com")
//                 .passwordHash(passwordEncoder.encode("admin123"))
//                 .fullName("Platform Administrator")
//                 .role(SystemUser.Role.PLATFORM_ADMIN)
//                 .status(SystemUser.UserStatus.APPROVED)
//                 .isActive(true)
//                 .build();
//         systemUserRepository.save(admin);

//         // Coordinator 1 - Helping Hands
//         SystemUser coordinator = SystemUser.builder()
//                 .username("coordinator")
//                 .email("coordinator@example.com")
//                 .passwordHash(passwordEncoder.encode("password123"))
//                 .fullName("Helping Hands Coordinator")
//                 .role(SystemUser.Role.ORGANIZATION_COORDINATOR)
//                 .status(SystemUser.UserStatus.APPROVED)
//                 .isActive(true)
//                 .build();
//         systemUserRepository.save(coordinator);

//         Organization helpingHands = Organization.builder()
//                 .name("Helping Hands")
//                 .mission("A non-profit organization helping the community.")
//                 .address("123 Main St")
//                 .contactEmail("contact@helpinghands.org")
//                 .status(Organization.OrganizationStatus.APPROVED)
//                 .coordinator(coordinator)
//                 .build();
//         helpingHands = organizationRepository.save(helpingHands);

//         coordinator.setOrganization(helpingHands);
//         systemUserRepository.save(coordinator);

//         // Coordinator 2 - Green Peace
//         SystemUser coordinator2 = SystemUser.builder()
//                 .username("coordinator2")
//                 .email("coord2@example.com")
//                 .passwordHash(passwordEncoder.encode("password123"))
//                 .fullName("Green Peace Coordinator")
//                 .role(SystemUser.Role.ORGANIZATION_COORDINATOR)
//                 .status(SystemUser.UserStatus.APPROVED)
//                 .isActive(true)
//                 .build();
//         systemUserRepository.save(coordinator2);

//         Organization greenPeace = Organization.builder()
//                 .name("Green Peace")
//                 .mission("Protecting the environment and promoting peace.")
//                 .address("456 Earth Way")
//                 .contactEmail("contact@greenpeace-demo.org")
//                 .status(Organization.OrganizationStatus.APPROVED)
//                 .coordinator(coordinator2)
//                 .build();
//         greenPeace = organizationRepository.save(greenPeace);

//         coordinator2.setOrganization(greenPeace);
//         systemUserRepository.save(coordinator2);

//         // Coordinator 3 - Safe Haven Animal Shelter
//         SystemUser coordinator3 = SystemUser.builder()
//                 .username("coordinator3")
//                 .email("coord3@example.com")
//                 .passwordHash(passwordEncoder.encode("password123"))
//                 .fullName("Safe Haven Coordinator")
//                 .role(SystemUser.Role.ORGANIZATION_COORDINATOR)
//                 .status(SystemUser.UserStatus.APPROVED)
//                 .isActive(true)
//                 .build();
//         systemUserRepository.save(coordinator3);

//         Organization safeHaven = Organization.builder()
//                 .name("Safe Haven Animal Shelter")
//                 .mission("Rescuing and rehoming abandoned animals.")
//                 .address("789 Pet Lane")
//                 .contactEmail("contact@safehaven-demo.org")
//                 .status(Organization.OrganizationStatus.APPROVED)
//                 .coordinator(coordinator3)
//                 .build();
//         safeHaven = organizationRepository.save(safeHaven);

//         coordinator3.setOrganization(safeHaven);
//         systemUserRepository.save(coordinator3);

//         // Volunteer
//         SystemUser volunteer = SystemUser.builder()
//                 .username("volunteer")
//                 .email("volunteer@example.com")
//                 .passwordHash(passwordEncoder.encode("password123"))
//                 .fullName("Demo Volunteer")
//                 .role(SystemUser.Role.VOLUNTEER)
//                 .status(SystemUser.UserStatus.APPROVED)
//                 .isActive(true)
//                 .build();
//         volunteer = systemUserRepository.save(volunteer);

//         // Six opportunities, one per organization theme, all OPEN/available
//         List<Opportunity> opportunities = new ArrayList<>();
//         opportunities.add(buildOpportunity("Food Drive", "Help sort and distribute food donations to families in need.",
//                 "Community Center", "Community Service", helpingHands));
//         opportunities.add(buildOpportunity("Tree Planting", "Plant trees in the local park to help restore green spaces.",
//                 "City Park", "Environment", greenPeace));
//         opportunities.add(buildOpportunity("Beach Cleanup", "Clean up litter and debris along the coastline.",
//                 "Sunset Beach", "Environment", greenPeace));
//         opportunities.add(buildOpportunity("Tutoring Kids", "Provide after-school tutoring for underprivileged children.",
//                 "Helping Hands Center", "Education", helpingHands));
//         opportunities.add(buildOpportunity("Animal Shelter Help", "Assist with feeding, walking, and caring for shelter animals.",
//                 "Safe Haven Shelter", "Animal Welfare", safeHaven));
//         opportunities.add(buildOpportunity("Elderly Care", "Spend time with elderly residents, assist with daily activities.",
//                 "Sunrise Senior Living", "Community Service", helpingHands));

//         opportunities = opportunityRepository.saveAll(opportunities);

//         // One enrollment per opportunity, covering all six EnrollmentStatus values
//         VolunteerEnrollment.EnrollmentStatus[] statuses = {
//                 VolunteerEnrollment.EnrollmentStatus.PENDING,
//                 VolunteerEnrollment.EnrollmentStatus.APPROVED,
//                 VolunteerEnrollment.EnrollmentStatus.STARTED,
//                 VolunteerEnrollment.EnrollmentStatus.COMPLETED,
//                 VolunteerEnrollment.EnrollmentStatus.REJECTED,
//                 VolunteerEnrollment.EnrollmentStatus.CANCELLED
//         };

//         for (int i = 0; i < opportunities.size(); i++) {
//             Opportunity opportunity = opportunities.get(i);
//             VolunteerEnrollment.EnrollmentStatus status = statuses[i];

//             VolunteerEnrollment enrollment = VolunteerEnrollment.builder()
//                     .volunteer(volunteer)
//                     .opportunity(opportunity)
//                     .status(status)
//                     .hoursLogged(status == VolunteerEnrollment.EnrollmentStatus.COMPLETED ? 4.0 : 0.0)
//                     .build();
//             enrollmentRepository.save(enrollment);

//             opportunity.setEnrolledCount(opportunity.getEnrolledCount() + 1);
//             opportunityRepository.save(opportunity);
//         }
//     }

//     private Opportunity buildOpportunity(String title, String description, String location, String category, Organization organization) {
//         return Opportunity.builder()
//                 .title(title)
//                 .description(description)
//                 .location(location)
//                 .category(category)
//                 .scheduledDate(LocalDate.now().plusDays(14))
//                 .startTime(LocalTime.of(9, 0))
//                 .endTime(LocalTime.of(13, 0))
//                 .maxCapacity(20)
//                 .enrolledCount(0)
//                 .status(Opportunity.OpportunityStatus.OPEN)
//                 .organization(organization)
//                 .build();
//     }
// }
