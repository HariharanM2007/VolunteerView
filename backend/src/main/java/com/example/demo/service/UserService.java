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
        SystemUser Role = new SystemUser();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUser(dto.getUser());

        return repo.save(user);
    }
}
