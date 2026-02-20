package com.exam.proctor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exam.proctor.config.JwtUtil;
import com.exam.proctor.dto.LoginRequestDTO;
import com.exam.proctor.dto.LoginResponseDTO;
import com.exam.proctor.dto.UserProfileDTO;
import com.exam.proctor.entity.User;
import com.exam.proctor.exception.CustomException;
import com.exam.proctor.exception.ResourceNotFoundException;
import com.exam.proctor.exception.UnauthorizedException;
import com.exam.proctor.repository.UserRepository;
import org.springframework.security.core.Authentication;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1️⃣ Find user by email 
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                new ResourceNotFoundException("Invalid email or password"));

        // 2️⃣ Check if user is active
        if (!user.isActive()) {
        	throw new CustomException("User account is deactivated");
        }

        // 3️⃣ Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        	throw new UnauthorizedException("Invalid email or password");
        }

        // 4️⃣ Generate JWT token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // 5️⃣ Return response
        return new LoginResponseDTO(
                token,
                user.getRole().name(),
                user.getEmail()
        );
    }
    
    public UserProfileDTO getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getBranch(),
                user.getCollegeName()
        );
    }
}
