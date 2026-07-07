package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
@Bean
public BCryptPasswordEncoder passwordEncoder(){

    return new BCryptPasswordEncoder();
    
}

@Bean
public SecurityFilterChain FilterChain(HttpSecurity http) throws exception {
    http.
}
    
}
