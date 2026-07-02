package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;

@Service
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

    public List<SystemUser> getAllUsers() {
        return repo.findAll();
    }

    public SystemUser getUser(Long id){
        return repo.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    }

    public SystemUser UpdateUser(Long id,RegisterDto dto){
        SystemUser user=getUser(id);

        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        

                return repo.save(user);
        
    }

    public void DeleteUser(Long id){
        SystemUser existingUser = getUser(id);
        repo.delete(existingUser);
    }
}
