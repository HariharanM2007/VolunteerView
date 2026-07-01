package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.SystemUser;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    public final UserService service;

    public UserController(UserService service){
        this.service=service;
    }

    @PostMapping
    public SystemUser createUser(@RequestBody RegisterDto dto){
        
        return service.createUser(dto);
    }
    
    

}