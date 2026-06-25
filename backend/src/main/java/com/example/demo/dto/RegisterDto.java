package com.example.demo.dto;

import com.example.demo.enums.Role;

import jakarta.validation.constraints.NotBlank;

public class RegisterDto {

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
    
    @NotBlank
    private String fullName;

    private String phoneNumber;
    
    @NotBlank
    private Role user; 

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getUser() {
        return user;
    }

    public void setUser(Role user) {
        this.user = user;
    }

    public RegisterDto(String username, String email, String password, String fullName, String phoneNumber, Role user){
        this.username=username;
        this.email=email;
        this.password=password;
        this.fullName=fullName;
        this.phoneNumber=phoneNumber;
        this.user=user;
    }

    
}