package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.RegisterDto;
import com.example.demo.service.UserService;

public class UserController {
    public final UserService service;

    public UserController(UserService service){
        this.service=service;
    }

    public SystemUser createUser(@RequestBody RegisterDto dto){
        return service.createUser(dto);
    }


}
