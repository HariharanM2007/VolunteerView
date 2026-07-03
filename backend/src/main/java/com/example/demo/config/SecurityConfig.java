package com.example.demo.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityConfig {
    
public BCryptPasswordEncoder passwordEncoder(){

    return new BCryptPasswordEncoder();
    
}


    
}
