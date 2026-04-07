package com.portfolio.club_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarErroresDeNegocio(IllegalArgumentException ex) {
        
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de validación: " + ex.getMessage());
    }
}