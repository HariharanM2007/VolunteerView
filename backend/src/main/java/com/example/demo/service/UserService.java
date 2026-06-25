package com.example.demo.service;

import com.example.demo.entity.SystemUser;
import com.example.demo.repository.SystemUserRepository;

public class UserService {
    public final SystemUserRepository repo;
    public UserService(SystemUserRepository repo){
        this.repo=repo;
    }

    public SystemUser create
}
