package com.exam.proctor.controller;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.exam.proctor.dto.LoginRequestDTO;
import com.exam.proctor.dto.UserProfileDTO;
import com.exam.proctor.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(
            Authentication authentication) {

        return ResponseEntity.ok(
                authService.getCurrentUser(authentication)
        );
    }
}

