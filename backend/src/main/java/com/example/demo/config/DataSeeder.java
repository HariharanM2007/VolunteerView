package com.example.demo.config;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Organization;
import com.example.demo.entity.SystemUser;
import com.example.demo.enums.OpportunityStatus;
import com.example.demo.enums.OrganizationStatus;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SystemUserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OpportunityRepository opportunityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        SystemUser admin = userRepository.findByUsername("admin")
                .orElseGet(() -> {

                    SystemUser user = SystemUser.builder()
                            .username("admin")
                            .email("admin@volunteerview.com")
                            .passwordHash(passwordEncoder.encode("Admin@123"))
                            .fullName("Platform Administrator")
                            .phoneNumber("9999999999")
                            .role(Role.PLATFORM_ADMIN)
                            .status(UserStatus.APPROVED)
                            .isActive(true)
                            .build();

                    return userRepository.save(user);
                });

        SystemUser coordinator = userRepository.findByUsername("coordinator")
                .orElseGet(() -> {

                    SystemUser user = SystemUser.builder()
                            .username("coordinator")
                            .email("coordinator@volunteerview.com")
                            .passwordHash(passwordEncoder.encode("Coordinator@123"))
                            .fullName("Organization Coordinator")
                            .phoneNumber("8888888888")
                            .role(Role.ORGANIZATION_COORDINATOR)
                            .status(UserStatus.APPROVED)
                            .isActive(true)
                            .build();

                    return userRepository.save(user);
                });

        /*
         * A coordinator can be associated with multiple organizations.
         * Therefore, do NOT use findByCoordinatorId(), which returns Optional
         * and fails when multiple organizations exist.
         *
         * findFirstByCoordinatorIdOrderByIdAsc() safely selects an existing
         * organization if one or more already exist.
         */
        Organization organization =
                organizationRepository
                        .findFirstByCoordinatorIdOrderByIdAsc(coordinator.getId())
                        .orElseGet(() -> {

                            Organization org = Organization.builder()
                                    .name("Helping Hands Foundation")
                                    .mission("Creating positive social impact through volunteering.")
                                    .address("Coimbatore, Tamil Nadu")
                                    .contactEmail("contact@helpinghands.org")
                                    .contactPhone("9876543210")
                                    .foundedYear(2022)
                                    .status(OrganizationStatus.APPROVED)
                                    .coordinator(coordinator)
                                    .build();

                            return organizationRepository.save(org);
                        });

        /*
         * Keep the coordinator assigned to the selected organization.
         */
        if (coordinator.getOrganization() == null) {

            coordinator.setOrganization(organization);

            userRepository.save(coordinator);
        }

        userRepository.findByUsername("volunteer")
                .orElseGet(() -> {

                    SystemUser volunteer = SystemUser.builder()
                            .username("volunteer")
                            .email("volunteer@volunteerview.com")
                            .passwordHash(passwordEncoder.encode("Volunteer@123"))
                            .fullName("Sample Volunteer")
                            .phoneNumber("7777777777")
                            .role(Role.VOLUNTEER)
                            .status(UserStatus.APPROVED)
                            .organization(organization)
                            .isActive(true)
                            .build();

                    return userRepository.save(volunteer);
                });

        if (opportunityRepository.count() == 0) {

            Opportunity opportunity = Opportunity.builder()
                    .title("Tree Plantation Drive")
                    .description("Join us in planting trees across the city.")
                    .location("Coimbatore")
                    .category("Environment")
                    .scheduledDate(LocalDate.now().plusDays(7))
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(12, 0))
                    .maxCapacity(50)
                    .requiredSkills("Teamwork")
                    .status(OpportunityStatus.OPEN)
                    .organization(organization)
                    .build();

            opportunityRepository.save(opportunity);
        }
    }
}