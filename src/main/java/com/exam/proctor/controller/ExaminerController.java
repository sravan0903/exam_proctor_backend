package com.exam.proctor.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.exam.proctor.dto.DeadlineDTO;
import com.exam.proctor.dto.ExamDTO;
import com.exam.proctor.dto.ExamResponseDTO;
import com.exam.proctor.dto.ExamStatsDTO;
import com.exam.proctor.dto.QuestionDTO;
import com.exam.proctor.dto.QuestionResponseDTO;
import com.exam.proctor.dto.ViolationLimitDTO;
import com.exam.proctor.entity.Exam;
import com.exam.proctor.service.ExaminerService;

@RestController
@RequestMapping("/examiner")
@CrossOrigin
public class ExaminerController {

	 @Autowired
	    private ExaminerService examinerService;

	    // 🔹 Create Exam
	    @PostMapping("/exam")
	    public ResponseEntity<Exam> createExam(
	            @RequestBody ExamDTO dto,
	            Authentication auth) {

	        return ResponseEntity.ok(
	                examinerService.createExam(dto, auth.getName())
	        );
	    }

	    // 🔹 My Exams
	    @GetMapping("/exams")
	    public ResponseEntity<List<ExamResponseDTO>> getMyExams(Authentication auth) {
	        //System.out.println(">>> Examiner exams API HIT");

	        return ResponseEntity.ok(
	            examinerService.getMyExams(auth.getName())
	        );
	    }
	    
	    // 🔹 Examiner Stats
	    @GetMapping("/stats")
	    @PreAuthorize("hasAuthority('EXAMINER')")
	    public ResponseEntity<ExamStatsDTO> getStats(Authentication auth) {
	        return ResponseEntity.ok(
	                examinerService.getStats(auth.getName())
	        );
	    }

	    // 🔹 Add Question
	    @PostMapping("/exam/{examId}/question")
	    public ResponseEntity<String> addQuestion(
	            @PathVariable Long examId,
	            @RequestBody QuestionDTO dto) {
	    	examinerService.addQuestion(examId, dto);
	        return ResponseEntity.ok("Question added successfully");
	    }

	    // 🔹 Get Questions
	    @GetMapping("/exam/{examId}/questions")
	    public ResponseEntity<List<QuestionResponseDTO>> getQuestions(
	            @PathVariable Long examId) {

	        return ResponseEntity.ok(
	                examinerService.getQuestions(examId)
	        );
	    }

	    // 🔹 Delete Question
	    @DeleteMapping("/question/{questionId}")
	    public ResponseEntity<Void> deleteQuestion(
	            @PathVariable Long questionId) {

	        examinerService.deleteQuestion(questionId);
	        return ResponseEntity.ok().build();
	    }

	    // 🔹 Block / Unblock Exam
	    @PutMapping("/exam/{examId}/status")
	    public ResponseEntity<Void> updateStatus(
	            @PathVariable Long examId,
	            @RequestParam boolean active) {

	        examinerService.updateExamStatus(examId, active);
	        return ResponseEntity.ok().build();
	    }

	    // 🔹 Set Violation Limit
	    @PutMapping("/exam/{examId}/violation-limit")
	    public ResponseEntity<Void> setViolationLimit(
	            @PathVariable Long examId,
	            @RequestBody ViolationLimitDTO dto) {

	        examinerService.updateViolationLimit(examId, dto.getViolationLimit());
	        return ResponseEntity.ok().build();
	    }

	    // 🔹 Set Deadline
	    @PutMapping("/exam/{examId}/deadline")
	    public ResponseEntity<Void> setDeadline(
	            @PathVariable Long examId,
	            @RequestBody DeadlineDTO dto) {

	        examinerService.updateDeadline(examId, dto.getDeadline());
	        return ResponseEntity.ok().build();
	    }
	    
	    @DeleteMapping("/exam/{examId}")
	    public ResponseEntity<Void> deleteExam(
	            @PathVariable Long examId,
	            Authentication authentication) {

	        examinerService.deleteExam(examId, authentication.getName());
	        return ResponseEntity.noContent().build();
	    }
	    
	    @PostMapping("/exam/{examId}/upload-questions")
	    public ResponseEntity<?> uploadQuestions(
	            @PathVariable Long examId,
	            @RequestParam("file") MultipartFile file) {

	        examinerService.uploadQuestionsFromCsv(examId, file);
	        return ResponseEntity.ok("Questions uploaded successfully");
	    }


}

