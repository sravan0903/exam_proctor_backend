package com.exam.proctor.controller;

import com.exam.proctor.dto.ExamAttemptResponseDTO;
import com.exam.proctor.dto.ExamSubmitResponseDTO;
import com.exam.proctor.dto.StudentAnswerDTO;
import com.exam.proctor.dto.StudentExamPlayDTO;
import com.exam.proctor.dto.StudentExamResponseDTO;
import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 🔹 View branch-based exams
    @GetMapping("/exams")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<StudentExamResponseDTO>> getAvailableExams(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getAvailableExams(authentication.getName())
        );
    }

    // 🔹 Get exam for exam player
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<StudentExamPlayDTO> getExamForPlayer(
            @PathVariable Long examId,
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getExamForPlayer(examId, authentication.getName())
        );
    }

    // 🔹 Start exam
    @PostMapping("/exam/{examId}/start")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> startExam(
            @PathVariable Long examId,
            Authentication authentication) {

        studentService.startExam(examId, authentication.getName());

        return ResponseEntity.ok(
                Map.of("message", "Exam started successfully")
        );
    }

    // 🔹 Submit exam
    @PostMapping("/exam/{examId}/submit")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<ExamSubmitResponseDTO> submitExam(
            @PathVariable Long examId,
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.submitExam(examId, authentication.getName())
        );
    }

    // 🔹 View my exam attempts
    @GetMapping("/attempts")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<List<ExamAttemptResponseDTO>> getMyAttempts(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentService.getMyAttempts(authentication.getName())
        );
    }

    // 🔹 Save answer
    @PostMapping("/answer")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> saveAnswer(
            @RequestBody StudentAnswerDTO dto,
            Authentication authentication) {

        studentService.saveAnswer(dto, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Answer saved"));
    }
}
