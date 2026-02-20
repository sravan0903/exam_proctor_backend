package com.exam.proctor.controller;


import com.exam.proctor.dto.ExamAttemptResponseDTO;
import com.exam.proctor.dto.UserCreateDTO;
import com.exam.proctor.dto.UserResponseDTO;
import com.exam.proctor.dto.ViolationResponseDTO;
import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.entity.User;
import com.exam.proctor.entity.Violation;
import com.exam.proctor.service.AdminService;
import com.exam.proctor.util.CsvGenerator;
import com.exam.proctor.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 🔹 Create Admin / Examiner / Student
    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(
            @RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(adminService.createUser(dto));
    }

    // 🔹 View all students
    @GetMapping("/students")
   
    public ResponseEntity<List<UserResponseDTO>> getStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    // 🔹 View all examiners
    @GetMapping("/examiners")
    
    public ResponseEntity<List<UserResponseDTO>> getExaminers() {
        return ResponseEntity.ok(adminService.getAllExaminers());
    }

    // 🔹 Activate / Deactivate user
    @PutMapping("/user/{id}/status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return ResponseEntity.ok(
                adminService.updateUserStatus(id, active)
        );
    }
    
    //View All Results
    @GetMapping("/reports/results")
    public ResponseEntity<List<ExamAttemptResponseDTO>> results() {
        return ResponseEntity.ok(adminService.getAllResults());
    }

    //Exam-wise Results
    @GetMapping("/reports/exam/{examId}")
    public ResponseEntity<List<ExamAttempt>> getResultsByExam(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                adminService.getResultsByExam(examId)
        );
    }

    //Student-wise Results
    @GetMapping("/reports/student/{studentId}")
    public ResponseEntity<List<ExamAttempt>> getResultsByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                adminService.getResultsByStudent(studentId)
        );
    }

    //View All Violations
    @GetMapping("/reports/violations")
    public ResponseEntity<List<ViolationResponseDTO>> getAllViolations() {
        return ResponseEntity.ok(adminService.getAllViolations());
    }

    //Violations by Exam
    @GetMapping("/reports/violations/exam/{examId}")
    public ResponseEntity<List<Violation>> getViolationsByExam(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                adminService.getViolationsByExam(examId)
        );
    }
    
    @GetMapping("/reports/export/csv")
    public void exportCsv(HttpServletResponse response) throws Exception {

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=exam_results.csv"
        );

        List<ExamAttemptResponseDTO> attempts = adminService.getAllResults();
        CsvGenerator.writeExamAttemptsToCsv(
                response.getWriter(),
                attempts
        );
    }
    
    @GetMapping("/reports/export/pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=exam_results.pdf"
        );

        List<ExamAttemptResponseDTO> attempts = adminService.getAllResults();
        PdfGenerator.writeExamAttemptsToPdf(
                response.getOutputStream(),
                attempts
        );
    }


}
