package com.example.demo.config;

import org.springdoc.core.properties.SwaggerUiConfigProperties.Csrf;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
@Bean
public BCryptPasswordEncoder passwordEncoder(){

    return new BCryptPasswordEncoder();
    
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http.csrf(CsrfConfigurer<HttpSecurity> csrf -> csrf.disable()).authorizeHttpRequests(AuthorizationManagerRequestMatcherRegistry auth -> auth.anyRequest())
}

    
}
