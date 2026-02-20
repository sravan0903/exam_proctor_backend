package com.exam.proctor.controller;

import com.exam.proctor.dto.ViolationDTO;
import com.exam.proctor.service.ProctorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/proctor")
@CrossOrigin
public class ProctorController {

    @Autowired
    private ProctorService proctorService;

    // 🔹 Called by frontend / AI
    @PostMapping("/violation")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> registerViolation(
            @RequestBody ViolationDTO dto,
            Authentication authentication) {

        String msg =
                proctorService.registerViolation(
                        dto,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                Map.of("message", msg)
        );
    }
}
