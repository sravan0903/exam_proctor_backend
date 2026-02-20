package com.exam.proctor.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.proctor.dto.ViolationDTO;
import com.exam.proctor.entity.Exam;
import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.entity.ExamStatus;
import com.exam.proctor.entity.User;
import com.exam.proctor.entity.Violation;
import com.exam.proctor.exception.CustomException;
import com.exam.proctor.exception.ResourceNotFoundException;
import com.exam.proctor.repository.ExamAttemptRepository;
import com.exam.proctor.repository.ExamRepository;
import com.exam.proctor.repository.UserRepository;
import com.exam.proctor.repository.ViolationRepository;

import jakarta.transaction.Transactional;

@Service
public class ProctorService {

    @Autowired
    private ViolationRepository violationRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentService studentService;

    // ✅ Register violation (JWT SAFE)
    @Transactional
    public String registerViolation(
            ViolationDTO dto,
            String studentEmail) {

        // 1️⃣ Fetch student from JWT
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        // 2️⃣ Fetch exam
        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        // 3️⃣ Fetch active attempt
        ExamAttempt attempt = examAttemptRepository
                .findByExamAndStudent(exam, student)
                .orElseThrow(() ->
                        new CustomException("Exam not started"));

        // 4️⃣ Ignore violations if already submitted
        if (attempt.getStatus() != ExamStatus.STARTED) {
            return "Exam already submitted";
        }

        // 5️⃣ 🔒 Prevent SPAM (same violation within last 5 seconds)
        LocalDateTime fiveSecondsAgo = LocalDateTime.now().minusSeconds(5);

        boolean alreadyExists =
                violationRepository
                        .existsByExamAttemptAndViolationTypeAndTimestampAfter(
                                attempt,
                                dto.getViolationType(),
                                fiveSecondsAgo
                        );

        if (alreadyExists) {
            return "Duplicate violation ignored";
        }

        // 6️⃣ Save violation (LINKED TO ATTEMPT)
        Violation violation = Violation.builder()
                .examAttempt(attempt)
                .violationType(dto.getViolationType()) // ENUM SAFE
                .timestamp(LocalDateTime.now())
                .build();

        violationRepository.save(violation);

        // 7️⃣ Count violations
        long count = violationRepository.countByExamAttempt(attempt);

        // 8️⃣ Auto-submit if limit exceeded
        if (count >= exam.getViolationLimit()) {

            studentService.autoSubmitExam(
                    exam.getId(),
                    studentEmail,
                    "Violation limit exceeded"
            );

            return "Violation limit exceeded. Exam auto-submitted.";
        }

        return "Violation recorded";
    }

}
