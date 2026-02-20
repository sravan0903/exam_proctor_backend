package com.exam.proctor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.proctor.dto.ExamAttemptResponseDTO;
import com.exam.proctor.dto.ExamSubmitResponseDTO;
import com.exam.proctor.dto.QuestionDTO;
import com.exam.proctor.dto.StudentAnswerDTO;
import com.exam.proctor.dto.StudentExamPlayDTO;
import com.exam.proctor.dto.StudentExamResponseDTO;
import com.exam.proctor.entity.Exam;
import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.entity.ExamStatus;
import com.exam.proctor.entity.Question;
import com.exam.proctor.entity.StudentAnswer;
import com.exam.proctor.entity.User;
import com.exam.proctor.exception.CustomException;
import com.exam.proctor.exception.ResourceNotFoundException;
import com.exam.proctor.repository.ExamAttemptRepository;
import com.exam.proctor.repository.ExamRepository;
import com.exam.proctor.repository.QuestionRepository;
import com.exam.proctor.repository.StudentAnswerRepository;
import com.exam.proctor.repository.UserRepository;

@Service
public class StudentService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /* =========================================================
       ✅ GET AVAILABLE EXAMS (BRANCH BASED)
       ========================================================= */
    public List<StudentExamResponseDTO> getAvailableExams(String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        List<Exam> exams = examRepository.findByBranch(student.getBranch());

        return exams.stream().map(exam -> {
            StudentExamResponseDTO dto = new StudentExamResponseDTO();
            dto.setId(exam.getId());
            dto.setExamName(exam.getExamName());
            dto.setBranch(exam.getBranch());
            dto.setDuration(exam.getDuration());
            dto.setActive(exam.isActive());
            dto.setTotalMarks(exam.getTotalMarks());
            return dto;
        }).toList();
    }


    /* =========================================================
       ✅ START EXAM (STRICT VALIDATION)
       ========================================================= */
    public ExamAttempt startExam(Long examId, String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        // Exam blocked
        if (!exam.isActive()) {
            throw new CustomException("This exam is currently blocked");
        }

        // Deadline check
        if (exam.getDeadline() != null &&
                LocalDateTime.now().isAfter(exam.getDeadline())) {
            throw new CustomException("Exam deadline has expired");
        }

        // Branch eligibility
        if (!exam.getBranch().equalsIgnoreCase(student.getBranch())) {
            throw new CustomException("You are not eligible for this exam");
        }

        // Prevent multiple attempts
        examAttemptRepository.findByExamAndStudent(exam, student)
                .ifPresent(a -> {
                    throw new CustomException("Exam already started or completed");
                });

        ExamAttempt attempt = ExamAttempt.builder()
                .exam(exam)
                .student(student)
                .status(ExamStatus.STARTED)
                .score(0)
                .startTime(LocalDateTime.now())
                .build();

        return examAttemptRepository.save(attempt);
    }

    /* =========================================================
       ✅ SAVE ANSWER (ONLY WHILE EXAM IS RUNNING)
       ========================================================= */
    public void saveAnswer(StudentAnswerDTO dto, String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found"));

        ExamAttempt attempt = examAttemptRepository
                .findByExamAndStudent(exam, student)
                .orElseThrow(() ->
                        new CustomException("Exam not started"));

        // Answers allowed ONLY when exam is STARTED
        if (attempt.getStatus() != ExamStatus.STARTED) {
            throw new CustomException("Cannot answer. Exam already submitted");
        }

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found"));

        StudentAnswer answer = studentAnswerRepository
                .findByExamAttemptAndQuestion(attempt, question)
                .orElse(
                        StudentAnswer.builder()
                                .examAttempt(attempt)
                                .question(question)
                                .build()
                );

        answer.setSelectedAnswer(dto.getSelectedAnswer());
        studentAnswerRepository.save(answer);
    }

    /* =========================================================
    ✅ MANUAL SUBMIT EXAM (DTO RESPONSE)
    ========================================================= */
 @Transactional
 public ExamSubmitResponseDTO submitExam(Long examId, String studentEmail) {

     User student = userRepository.findByEmail(studentEmail)
             .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

     Exam exam = examRepository.findById(examId)
             .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

     ExamAttempt attempt = examAttemptRepository
             .findByExamAndStudent(exam, student)
             .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

     // ✅ Idempotent (VERY IMPORTANT)
     if (attempt.getStatus() == ExamStatus.STARTED) {

         int finalScore;
         try {
             finalScore = calculateScore(attempt);
         } catch (Exception e) {
             finalScore = 0;
         }

         attempt.setScore(finalScore);
         attempt.setStatus(ExamStatus.SUBMITTED);
         attempt.setEndTime(LocalDateTime.now());

         examAttemptRepository.save(attempt);
     }

     // ✅ ALWAYS RETURN DTO (NEVER ENTITY)
     return ExamSubmitResponseDTO.builder()
             .attemptId(attempt.getId())
             .examId(exam.getId())
             .examName(exam.getExamName())
             .score(attempt.getScore())
             .status(attempt.getStatus())
             .startTime(attempt.getStartTime())
             .endTime(attempt.getEndTime())
             .build();
 }



    /* =========================================================
       ✅ AUTO SUBMIT (AI / TIMER / VIOLATIONS)
       ========================================================= */
 @Transactional
 public ExamSubmitResponseDTO autoSubmitExam(
         Long examId,
         String studentEmail,
         String reason) {

     User student = userRepository.findByEmail(studentEmail)
             .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

     Exam exam = examRepository.findById(examId)
             .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

     ExamAttempt attempt = examAttemptRepository
             .findByExamAndStudent(exam, student)
             .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

     // ✅ Idempotent check
     if (attempt.getStatus() != ExamStatus.STARTED) {
         return ExamSubmitResponseDTO.builder()
                 .attemptId(attempt.getId())
                 .examId(exam.getId())
                 .examName(exam.getExamName())
                 .score(attempt.getScore())
                 .status(attempt.getStatus())
                 .startTime(attempt.getStartTime())
                 .endTime(attempt.getEndTime())
                 .build();
//         .submitType("AUTO")
//         .reason(reason)
     }

     int finalScore;
     try {
         finalScore = calculateScore(attempt);
     } catch (Exception e) {
         finalScore = 0; // fail-safe
     }

     attempt.setScore(finalScore);
     attempt.setStatus(ExamStatus.AUTO_SUBMITTED);
     attempt.setEndTime(LocalDateTime.now());

     examAttemptRepository.save(attempt);

     // ✅ RETURN DTO (NOT ENTITY)
     return ExamSubmitResponseDTO.builder()
             .attemptId(attempt.getId())
             .examId(exam.getId())
             .examName(exam.getExamName())
             .score(finalScore)
             .status(ExamStatus.AUTO_SUBMITTED)
             .startTime(attempt.getStartTime())
             .endTime(attempt.getEndTime())
             .build();
//     .submitType("AUTO")
//     .reason(reason)
 }



    /* =========================================================
       ✅ VIEW MY EXAM ATTEMPTS / RESULTS
       ========================================================= */
    public List<ExamAttemptResponseDTO> getMyAttempts(String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        List<ExamAttempt> attempts =
        		examAttemptRepository.findByStudentWithExam(student);

        return attempts.stream().map(attempt -> {

            ExamAttemptResponseDTO dto = new ExamAttemptResponseDTO();
            dto.setId(attempt.getId());
            dto.setExamId(attempt.getExam().getId());
            dto.setExamName(attempt.getExam().getExamName());
            dto.setScore(attempt.getScore());
            dto.setTotalMarks(attempt.getExam().getTotalMarks());
            dto.setStatus(attempt.getStatus());
            dto.setStartTime(attempt.getStartTime());
            dto.setEndTime(attempt.getEndTime());

            return dto;
        }).toList();
    }


    /* =========================================================
       🔢 SCORE CALCULATION (PRIVATE)
       ========================================================= */
    int calculateScore(ExamAttempt attempt) {

        List<StudentAnswer> answers =
                studentAnswerRepository.findByExamAttempt(attempt);

        if (answers == null || answers.isEmpty()) {
            return 0; // No answers = zero score
        }

        int score = 0;

        for (StudentAnswer answer : answers) {

            if (answer == null) continue;
            if (answer.getQuestion() == null) continue;
            if (answer.getSelectedAnswer() == null) continue;
            if (answer.getQuestion().getCorrectAnswer() == null) continue;

            if (answer.getSelectedAnswer()
                    .equalsIgnoreCase(
                            answer.getQuestion().getCorrectAnswer())) {
                score++;
            }
        }

        return score;
    }

    
    @Transactional(readOnly = true)
    public StudentExamPlayDTO getExamForPlayer(Long examId, String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Exam exam = examRepository.findByIdWithQuestions(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // eligibility checks (same as startExam)
        if (!exam.getBranch().equalsIgnoreCase(student.getBranch())) {
            throw new CustomException("You are not eligible for this exam");
        }

        StudentExamPlayDTO dto = new StudentExamPlayDTO();
        dto.setId(exam.getId());
        dto.setExamName(exam.getExamName());
        dto.setDuration(exam.getDuration());
        dto.setViolationLimit(exam.getViolationLimit());

        dto.setQuestions(
            exam.getQuestions().stream().map(q -> {
                QuestionDTO qdto = new QuestionDTO();
                qdto.setQuestionId(q.getId());
                qdto.setQuestion(q.getQuestion());
                qdto.setOptionA(q.getOptionA());
                qdto.setOptionB(q.getOptionB());
                qdto.setOptionC(q.getOptionC());
                qdto.setOptionD(q.getOptionD());
                return qdto;
            }).toList()
        );

        return dto;
    }

}
