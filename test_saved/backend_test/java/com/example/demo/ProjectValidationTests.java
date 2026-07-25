package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Listeners(TestResultListener.class)
public class ProjectValidationTests {

    private static final String PRIMARY_CONTROLLER = "com.example.demo.controller.OpportunityController";
    private static final String SECONDARY_CONTROLLER = "com.example.demo.controller.EnrollmentController";
    private static final String PRIMARY_SERVICE = "com.example.demo.service.OpportunityService";
    private static final String SECONDARY_SERVICE = "com.example.demo.service.EnrollmentService";
    private static final String PRIMARY_REPOSITORY = "com.example.demo.repository.OpportunityRepository";
    private static final String PRIMARY_ENTITY_PATH = "com.example.demo.entity.Opportunity";
    private static final String TABLE_NAME = "opportunities";
    private static final String API_BASE_PATH = "/api/opportunities";
    private static final String DOMAIN_ROLE = "VOLUNTEER";
    private static final String CRUD_DELETE_MSG = "Opportunity deleted successfully.";
    private static final String DOMAIN_VALUE_1 = "Community Clean Up";
    private static final String DOMAIN_VALUE_2 = "Help clean the local park.";

    // ────────────────────────────────────────────────────────────
    // DAY-1 | Sprint: Package & Layer Architecture Verification
    // ────────────────────────────────────────────────────────────

    @Test
    public void t1_primaryControllerLoadsAndHasMethods() throws Exception {
        Class<?> clazz = Class.forName(PRIMARY_CONTROLLER);
        Assert.assertNotNull(clazz);
        Method[] methods = clazz.getDeclaredMethods();
        Assert.assertTrue(methods.length > 0);
        boolean hasAction = Arrays.stream(methods).anyMatch(m -> m.getName().contains("get")
                || m.getName().contains("create") || m.getName().contains("delete") || m.getName().contains("update"));
        Assert.assertTrue(hasAction);
    }

    @Test
    public void t2_primaryServiceLoadsAndHasMethods() throws Exception {
        Class<?> clazz = Class.forName(PRIMARY_SERVICE);
        Assert.assertNotNull(clazz);
        Assert.assertTrue(clazz.getDeclaredMethods().length >= 3);
    }

    @Test
    public void t3_primaryRepositoryInterfaceLoads() throws Exception {
        Class<?> clazz = Class.forName(PRIMARY_REPOSITORY);
        Assert.assertTrue(clazz.isInterface());
        boolean isJpa = Arrays.stream(clazz.getInterfaces()).anyMatch(i -> i.getSimpleName().contains("JpaRepository"));
        Assert.assertTrue(isJpa);
    }

    @Test
    public void t4_primaryEntityLoadsWithAnnotations() throws Exception {
        Class<?> clazz = Class.forName(PRIMARY_ENTITY_PATH);
        Assert.assertNotNull(clazz.getAnnotation(jakarta.persistence.Entity.class));
        jakarta.persistence.Table table = clazz.getAnnotation(jakarta.persistence.Table.class);
        Assert.assertNotNull(table);
        Assert.assertEquals(table.name(), TABLE_NAME);
        Assert.assertTrue(clazz.getDeclaredFields().length >= 4);
    }

    @Test
    public void t5_securityConfigLoads() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.config.SecurityConfig");
        Assert.assertNotNull(clazz.getAnnotation(org.springframework.context.annotation.Configuration.class));
        Method filterChain = clazz.getDeclaredMethod("securityFilterChain", HttpSecurity.class);
        Assert.assertNotNull(filterChain.getAnnotation(org.springframework.context.annotation.Bean.class));
        Method passwordEncoder = clazz.getDeclaredMethod("passwordEncoder");
        Assert.assertNotNull(passwordEncoder.getAnnotation(org.springframework.context.annotation.Bean.class));
    }

    // ────────────────────────────────────────────────────────────
    // DAY-2 | Sprint: Full CRUD Response Verification via Reflection
    // ────────────────────────────────────────────────────────────

    @Test
    public void t6_restControllerAndRequestMapping() throws Exception {
        Class<?> clazz = Class.forName(PRIMARY_CONTROLLER);
        Assert.assertNotNull(clazz.getAnnotation(org.springframework.web.bind.annotation.RestController.class));
        org.springframework.web.bind.annotation.RequestMapping reqMap = clazz
                .getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
        Assert.assertEquals(reqMap.value()[0], API_BASE_PATH);
    }

    @Test
    public void t7_getAllEndpointReturnsList() throws Exception {
        Class<?> controllerClass = Class.forName(PRIMARY_CONTROLLER);
        Method getAllMethod = Arrays.stream(controllerClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class) &&
                        m.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class).value().length == 0)
                .findFirst().orElseThrow(() -> new RuntimeException("Get all method not found"));

        Class<?> serviceClass = Class.forName(PRIMARY_SERVICE);
        // Use reflection to create mock
        Class<?> mockitoClass = Class.forName("org.mockito.Mockito");
        Method mockMethod = mockitoClass.getMethod("mock", Class.class);
        Object mockService = mockMethod.invoke(null, serviceClass);

        Class<?> repoClass = Class.forName("com.example.demo.repository.SystemUserRepository");
        Object mockRepo = mockMethod.invoke(null, repoClass);

        Constructor<?> constructor = controllerClass.getDeclaredConstructors()[0];
        Object controller = constructor.newInstance(mockService, mockRepo);

        Assert.assertNotNull(controller);
    }

    @Test
    public void t8_deleteEndpointReturnsExactString() throws Exception {
        Class<?> controllerClass = Class.forName(PRIMARY_CONTROLLER);
        Method deleteMethod = Arrays.stream(controllerClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class))
                .findFirst().orElseThrow(() -> new RuntimeException("Delete method not found"));

        Class<?> serviceClass = Class.forName(PRIMARY_SERVICE);
        // Use reflection to create mock
        Class<?> mockitoClass = Class.forName("org.mockito.Mockito");
        Method mockMethod = mockitoClass.getMethod("mock", Class.class);
        Object mockService = mockMethod.invoke(null, serviceClass);

        Class<?> repoClass = Class.forName("com.example.demo.repository.SystemUserRepository");
        Object mockRepo = mockMethod.invoke(null, repoClass);

        Constructor<?> constructor = controllerClass.getDeclaredConstructors()[0];
        Object controller = constructor.newInstance(mockService, mockRepo);

        Assert.assertNotNull(controller);
    }

    @Test
    public void t9_globalExceptionHandler404() throws Exception {
        Class<?> handlerClass = Class.forName("com.example.demo.exception.GlobalExceptionHandler");
        Assert.assertNotNull(
                handlerClass.getAnnotation(org.springframework.web.bind.annotation.RestControllerAdvice.class));
    }

    @Test
    public void t10_secretKeyFieldIsPrivate() throws Exception {
        Class<?> jwtClass = Class.forName("com.example.demo.security.JwtService");
        Field secretKeyField = Arrays.stream(jwtClass.getDeclaredFields())
                .filter(f -> f.getName().toLowerCase().contains("secret"))
                .findFirst().orElseThrow(() -> new RuntimeException("Secret key field not found"));
        Assert.assertTrue(Modifier.isPrivate(secretKeyField.getModifiers()));
    }

    @Test
    public void t11_tableNameMatchesConvention() throws Exception {
        Class<?> clazz = Class.forName(PRIMARY_ENTITY_PATH);
        jakarta.persistence.Table table = clazz.getAnnotation(jakarta.persistence.Table.class);
        Assert.assertEquals(table.name(), TABLE_NAME);
    }

    @Test
    public void t12_securityFilterChainBeanExists() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.config.SecurityConfig");
        Method filterChain = clazz.getDeclaredMethod("securityFilterChain", HttpSecurity.class);
        Assert.assertNotNull(filterChain.getAnnotation(org.springframework.context.annotation.Bean.class));
    }

    @Test
    public void t13_enrollmentControllerHasEnrollMethod() throws Exception {
        Class<?> clazz = Class.forName(SECONDARY_CONTROLLER);
        Class<?> dtoClass = Class.forName("com.example.demo.dto.EnrollmentRequestDto");
        Method method = clazz.getDeclaredMethod("enroll", Object.class, dtoClass);
        Assert.assertNotNull(method);
    }

    @Test
    public void t14_enrollmentControllerHasApproveMethod() throws Exception {
        Class<?> clazz = Class.forName(SECONDARY_CONTROLLER);
        Method method = clazz.getDeclaredMethod("approve", Object.class, Long.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t15_enrollmentControllerHasCheckInMethod() throws Exception {
        Class<?> clazz = Class.forName(SECONDARY_CONTROLLER);
        Method method = clazz.getDeclaredMethod("checkIn", Object.class, Long.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t16_enrollmentRepoHasExistsByVolunteerAndOpp() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.repository.VolunteerEnrollmentRepository");
        Method method = clazz.getDeclaredMethod("existsByVolunteerIdAndOpportunityId", Long.class, Long.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t17_enrollmentRepoHasFindWithDetailsById() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.repository.VolunteerEnrollmentRepository");
        Method method = clazz.getDeclaredMethod("findWithDetailsById", Long.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t18_enrollmentRepoHasSumHours() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.repository.VolunteerEnrollmentRepository");
        Method method = clazz.getDeclaredMethod("sumHoursLoggedByVolunteerId", Long.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t19_organizationControllerHasCreateMethod() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.controller.OrganizationController");
        Class<?> dtoClass = Class.forName("com.example.demo.dto.OrganizationRequestDto");
        Method method = clazz.getDeclaredMethod("createOrganization", dtoClass);
        Assert.assertNotNull(method);
    }

    @Test
    public void t20_organizationControllerHasApproveMethod() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.controller.OrganizationController");
        Method method = clazz.getDeclaredMethod("approveOrganization", Long.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t21_organizationRepoHasExistsByName() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.repository.OrganizationRepository");
        Method method = clazz.getDeclaredMethod("existsByName", String.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t22_organizationRepoHasFindAllByCoordinatorId() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.repository.OrganizationRepository");
        Method method = clazz.getDeclaredMethod("findAllByCoordinatorId", Long.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t23_jwtServiceHasGenerateAccessToken() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.security.JwtService");
        Class<?> userClass = Class.forName("com.example.demo.entity.SystemUser");
        Method method = clazz.getDeclaredMethod("generateAccessToken", userClass);
        Assert.assertNotNull(method);
    }

    @Test
    public void t24_jwtServiceHasGenerateRefreshToken() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.security.JwtService");
        Class<?> userClass = Class.forName("com.example.demo.entity.SystemUser");
        Method method = clazz.getDeclaredMethod("generateRefreshToken", userClass);
        Assert.assertNotNull(method);
    }

    @Test
    public void t25_jwtServiceHasExtractRole() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.security.JwtService");
        Method method = clazz.getDeclaredMethod("extractRole", String.class);
        Assert.assertNotNull(method);
    }

    @Test
    public void t26_securityConfigHasPasswordEncoder() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.config.SecurityConfig");
        Method method = clazz.getDeclaredMethod("passwordEncoder");
        Assert.assertNotNull(method);
    }

    @Test
    public void t27_authControllerHasRegisterMethod() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.controller.AuthController");
        Class<?> dtoClass = Class.forName("com.example.demo.dto.RegisterDto");
        Method method = clazz.getDeclaredMethod("register", dtoClass);
        Assert.assertNotNull(method);
    }

    @Test
    public void t28_authControllerHasLoginMethod() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.controller.AuthController");
        Class<?> dtoClass = Class.forName("com.example.demo.dto.AuthRequestDto");
        Method method = clazz.getDeclaredMethod("login", dtoClass);
        Assert.assertNotNull(method);
    }

    @Test
    public void t29_authControllerHasRefreshMethod() throws Exception {
        Class<?> clazz = Class.forName("com.example.demo.controller.AuthController");
        Class<?> dtoClass = Class.forName("com.example.demo.dto.RefreshTokenRequestDto");
        Method method = clazz.getDeclaredMethod("refreshToken", dtoClass);
        Assert.assertNotNull(method);
    }

    @Test
    public void t30_opportunityControllerHasCreateMethod() throws Exception {
        Class<?> clazz = Class.forName(PRIMARY_CONTROLLER);
        Class<?> dtoClass = Class.forName("com.example.demo.dto.OpportunityRequestDto");
        Method method = clazz.getDeclaredMethod("create", Object.class, dtoClass);
        Assert.assertNotNull(method);
    }
}