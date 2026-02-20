package com.exam.proctor.service;


import com.exam.proctor.dto.ExamDTO;
import com.exam.proctor.dto.ExamResponseDTO;
import com.exam.proctor.dto.ExamStatsDTO;
import com.exam.proctor.dto.QuestionDTO;
import com.exam.proctor.dto.QuestionResponseDTO;
import com.exam.proctor.entity.Exam;
import com.exam.proctor.entity.Question;
import com.exam.proctor.entity.User;
import com.exam.proctor.exception.CustomException;
import com.exam.proctor.exception.ResourceNotFoundException;
import com.exam.proctor.repository.ExamAttemptRepository;
import com.exam.proctor.repository.ExamRepository;
import com.exam.proctor.repository.QuestionRepository;
import com.exam.proctor.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExaminerService {

	 @Autowired private ExamRepository examRepo;
	    @Autowired private QuestionRepository questionRepo;
	    @Autowired private UserRepository userRepo;
	    @Autowired private ExamAttemptRepository examAttemptRepo;

	    public Exam createExam(ExamDTO dto, String email) {

	        User examiner = userRepo.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Examiner not found"));

	        Exam exam = new Exam();

	        exam.setExamName(dto.getExamName());
	        exam.setBranch(dto.getBranch());
	        exam.setDuration(dto.getDuration());
	        exam.setTotalMarks(dto.getTotalMarks());
	        exam.setViolationLimit(dto.getViolationLimit());
	        exam.setExaminer(examiner);
	        exam.setActive(true);

	        // ✅ AUTO SET DEADLINE LOGIC
	        if (dto.getDeadline() != null) {
	            exam.setDeadline(dto.getDeadline());
	        } else {
	            // Default: deadline = now + 7 days
	            exam.setDeadline(java.time.LocalDateTime.now().plusDays(7));
	        }

	        return examRepo.save(exam);
	    }


	    public List<ExamResponseDTO> getMyExams(String email) {
	        try {
	            List<Exam> exams = examRepo.findByExaminer_Email(email);

	            return exams.stream().map(exam -> {
	                ExamResponseDTO dto = new ExamResponseDTO();
	                dto.setId(exam.getId());
	                dto.setExamName(exam.getExamName());
	                dto.setBranch(exam.getBranch());
	                dto.setDeadline(exam.getDeadline());
	                dto.setDuration(exam.getDuration());
	                dto.setTotalMarks(exam.getTotalMarks());
	                dto.setViolationLimit(exam.getViolationLimit());
	                return dto;
	            }).collect(Collectors.toList());

	        } catch (Exception e) {
	            e.printStackTrace(); // 🔥 keep this during debugging
	            throw e;
	        }
	    }


	    public ExamStatsDTO getStats(String email) {

	        List<Exam> exams = examRepo.findByExaminer_Email(email);

	        List<Long> examIds = exams.stream()
	                .map(Exam::getId)
	                .toList();

	        long totalQuestions = examIds.isEmpty()
	                ? 0
	                : questionRepo.countByExamIdIn(examIds);

	        long activeExams = exams.stream()
	                .filter(Exam::isActive)
	                .count();

	        return new ExamStatsDTO(
	                exams.size(),
	                activeExams,
	                totalQuestions
	        );
	    }


	    public Question addQuestion(Long examId, QuestionDTO dto) {
	        Exam exam = examRepo.findById(examId).orElseThrow();

	        Question q = new Question();
	        q.setQuestion(dto.getQuestion());
	        q.setOptionA(dto.getOptionA());
	        q.setOptionB(dto.getOptionB());
	        q.setOptionC(dto.getOptionC());
	        q.setOptionD(dto.getOptionD());
	        q.setCorrectAnswer(dto.getCorrectAnswer());
	        q.setExam(exam);

	        return questionRepo.save(q);
	    }

	    public List<QuestionResponseDTO> getQuestions(Long examId) {

	    	 List<Question> questions = questionRepo.findByExamId(examId);

	        return questions.stream().map(q -> {
	            QuestionResponseDTO dto = new QuestionResponseDTO();
	            dto.setId(q.getId());
	            dto.setQuestionText(q.getQuestion());
	            dto.setOptionA(q.getOptionA());
	            dto.setOptionB(q.getOptionB());
	            dto.setOptionC(q.getOptionC());
	            dto.setOptionD(q.getOptionD());
	            dto.setCorrectAnswer(q.getCorrectAnswer());
	            return dto;
	        }).toList();
	    }


	    public void deleteQuestion(Long questionId) {
	        questionRepo.deleteById(questionId);
	    }

	    public void updateExamStatus(Long examId, boolean active) {
	        Exam exam = examRepo.findById(examId).orElseThrow();
	        exam.setActive(active);
	        examRepo.save(exam);
	    }

	    public void updateViolationLimit(Long examId, int limit) {
	        Exam exam = examRepo.findById(examId).orElseThrow();
	        exam.setViolationLimit(limit);
	        examRepo.save(exam);
	    }

	    public void updateDeadline(Long examId, LocalDateTime deadline) {
	        Exam exam = examRepo.findById(examId).orElseThrow();
	        exam.setDeadline(deadline);
	        examRepo.save(exam);
	    }
	    
	    @Transactional
	    public void deleteExam(Long examId, String examinerEmail) {

	        Exam exam = examRepo.findById(examId)
	                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

	        // 🔐 Ownership check
	        if (!exam.getExaminer().getEmail().equals(examinerEmail)) {
	            throw new CustomException("You are not authorized to delete this exam");
	        }

	        // ❌ Prevent deletion if attempts exist
	        if (examAttemptRepo.existsByExam(exam)) {
	            throw new CustomException(
	                    "Cannot delete exam. Students have already attempted it.");
	        }

	        // ✅ Safe delete (questions cascade)
	        examRepo.delete(exam);
	    }
	    
	    @Transactional
	    public void uploadQuestionsFromCsv(Long examId, MultipartFile file) {

	        Exam exam = examRepo.findById(examId)
	                .orElseThrow(() -> new RuntimeException("Exam not found"));

	        List<Question> questionList = new ArrayList<>();

	        try (BufferedReader reader =
	                new BufferedReader(new InputStreamReader(file.getInputStream()))) {

	            String line;
	            boolean isFirstLine = true;

	            while ((line = reader.readLine()) != null) {

	                // Skip header
	                if (isFirstLine) {
	                    isFirstLine = false;
	                    continue;
	                }

	                String[] data = line.split(",");

	                if (data.length < 6) {
	                    continue; // skip invalid rows
	                }

	                Question q = new Question();
	                q.setExam(exam);
	                q.setQuestion(data[0].trim());
	                q.setOptionA(data[1].trim());
	                q.setOptionB(data[2].trim());
	                q.setOptionC(data[3].trim());
	                q.setOptionD(data[4].trim());
	                q.setCorrectAnswer(data[5].trim());

	                questionList.add(q);
	            }

	            questionRepo.saveAll(questionList); // Batch save ✅

	        } catch (Exception e) {
	            throw new RuntimeException("Error processing CSV file: " + e.getMessage());
	        }
	    }

}
