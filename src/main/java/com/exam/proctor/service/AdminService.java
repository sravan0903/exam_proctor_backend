package com.exam.proctor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.proctor.dto.ExamAttemptResponseDTO;
import com.exam.proctor.dto.UserCreateDTO;
import com.exam.proctor.dto.UserResponseDTO;
import com.exam.proctor.dto.ViolationResponseDTO;
import com.exam.proctor.entity.Exam;
import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.entity.Role;
import com.exam.proctor.entity.User;
import com.exam.proctor.entity.Violation;
import com.exam.proctor.repository.ExamAttemptRepository;
import com.exam.proctor.repository.ExamRepository;
import com.exam.proctor.repository.UserRepository;
import com.exam.proctor.repository.ViolationRepository;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private ViolationRepository violationRepository;

    @Autowired
    private ExamRepository examRepository;

    /* ===============================
       👤 USER MANAGEMENT
       =============================== */

    public User createUser(UserCreateDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .collegeName(dto.getCollegeName())
                .branch(dto.getBranch())
                .fatherName(dto.getFatherName())
                .role(dto.getRole())
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public List<UserResponseDTO> getAllStudents() {

        List<User> users = userRepository.findByRole(Role.STUDENT);

        return users.stream().map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .collegeName(user.getCollegeName())
                        .branch(user.getBranch())
                        .fatherName(user.getFatherName())
                        .role(user.getRole())
                        .active(user.isActive())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> getAllExaminers() {

        List<User> users = userRepository.findByRole(Role.EXAMINER);

        return users.stream().map(user ->
                UserResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .collegeName(user.getCollegeName())
                        .branch(user.getBranch())
                        .fatherName(user.getFatherName())
                        .role(user.getRole())
                        .active(user.isActive())
                        .build()
        ).toList();
    }

    public User updateUserStatus(Long userId, boolean active) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(active);
        return userRepository.save(user);
    }

    /* ===============================
       📊 RESULTS REPORTS
       =============================== */

    // 🌍 Global Results
    @Transactional(readOnly = true)
    public List<ExamAttemptResponseDTO> getAllResults() {

        List<ExamAttempt> attempts =
                examAttemptRepository.findAllWithExamAndStudent();

        List<ExamAttemptResponseDTO> response = new ArrayList<>();

        for (ExamAttempt attempt : attempts) {

            ExamAttemptResponseDTO dto = ExamAttemptResponseDTO.builder()
                    .id(attempt.getId())
                    .examId(attempt.getExam().getId())
                    .examName(attempt.getExam().getExamName())
                    .studentId(attempt.getStudent().getId())
                    .studentName(attempt.getStudent().getName())
                    .branch(attempt.getStudent().getBranch())
                    .score(attempt.getScore())
                    .status(attempt.getStatus())
                    .startTime(attempt.getStartTime())
                    .endTime(attempt.getEndTime())
                    .build();

            response.add(dto);
        }

        return response;
    }


    // 📘 Exam-wise Results
    public List<ExamAttempt> getResultsByExam(Long examId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        return examAttemptRepository.findByExam(exam);
    }

    // 👨‍🎓 Student-wise Results
    public List<ExamAttempt> getResultsByStudent(Long studentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return examAttemptRepository.findByStudent(student);
    }

    /* ===============================
       🚨 VIOLATION REPORTS (FIXED)
       =============================== */

    // 🌍 All Violations (Admin dashboard)
    public List<ViolationResponseDTO> getAllViolations() {

    	List<Violation> violations = violationRepository.findAllWithDetails();

        List<ViolationResponseDTO> response = new ArrayList<>();

        for (Violation violation : violations) {

            ExamAttempt attempt = violation.getExamAttempt();

            response.add(
                ViolationResponseDTO.builder()
                    .id(violation.getId())
                    .violationType(violation.getViolationType())
                    .timestamp(violation.getTimestamp())

                    .examId(attempt.getExam().getId())
                    .examName(attempt.getExam().getExamName())

                    .studentId(attempt.getStudent().getId())
                    .studentName(attempt.getStudent().getName())
                    .build()
            );
        }

        return response;
    }


    // 📘 Exam-wise Violations (CORRECT)
    public List<Violation> getViolationsByExam(Long examId) {

        // uses custom JPQL in repository
        return violationRepository.findByExamId(examId);
    }

    // 👨‍🎓 Student-wise Violations (OPTIONAL BUT USEFUL)
    public List<Violation> getViolationsByStudent(Long studentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<ExamAttempt> attempts =
                examAttemptRepository.findByStudent(student);

        List<Violation> violations = new ArrayList<>();

        for (ExamAttempt attempt : attempts) {
            violations.addAll(
                violationRepository.findByExamAttempt(attempt)
            );
        }

        return violations;
    }
}
