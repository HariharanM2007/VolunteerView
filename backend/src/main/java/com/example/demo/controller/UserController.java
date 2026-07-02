package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    @GetMapping
    public ResponseEntity<List<SystemUser>> getAllUsers(){

        List<SystemUser> users = service.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
        
    }



}