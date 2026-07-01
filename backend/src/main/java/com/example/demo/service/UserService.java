package com.example.demo.service;

import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;

public class UserService {
    public final SystemUserRepository repo;
    public UserService(SystemUserRepository repo){
        this.repo=repo;
    }

    public SystemUser createUser(RegisterDto dto){
        SystemUser RoleUser = new SystemUser();

        RoleUser.setUsername(dto.getUsername());
        RoleUser.setEmail(dto.getEmail());
        RoleUser.setPasswordHash(dto.getPassword());
        RoleUser.setFullName(dto.getFullName());
        RoleUser.setPhoneNumber(dto.getPhoneNumber());
        RoleUser.setUser(dto.getUser());

        return repo.save(RoleUser);
    }
}
