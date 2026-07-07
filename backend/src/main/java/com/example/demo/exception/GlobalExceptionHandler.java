package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>>handleValidation(MethodArgumentNotValidException ex){
        Map<String,Object>errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error->{
            Map<String,Object>details=new HashMap<>();
            details.put("Field",error.getField());
            details.put("Rejected Value",error.getRejectedValue());
            details.put("Message", error.getDefaultMessage());
            details.put("Validation Type",error.getCode());

            errors.put(error.getField(),details);

        });
        return ResponseEntity.badRequest().body(errors);
    }
  
}