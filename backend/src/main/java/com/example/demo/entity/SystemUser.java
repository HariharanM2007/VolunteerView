package com.example.demo.entity;

import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;

import jakarta.persistence.*;

@Entity
@Table(name="system_users")

public class SystemUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    
    @Column(unique = true)
    private String username;

    private String email;

    private String passwordHash;

    private String fullName;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role user;
    private UserStatus use;

    public SystemUser(){

    }

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }

    public String getPasswordHash(){
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash=passwordHash;
    }

    public String getFullName(){
        return fullName;
    }
    public void setFullName(String fullName){
        this.fullName=fullName;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }

    public Role getUser(){
        return user;
    }
    public void setUser(Role user){
        this.user=user;
    }

    public UserStatus getUse() {
        return use;
    }

    public void setUse(UserStatus use) {
        this.use = use;
    }    
}
