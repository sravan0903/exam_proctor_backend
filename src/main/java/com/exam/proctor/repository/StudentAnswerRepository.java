package com.exam.proctor.repository;


import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface StudentAnswerRepository
        extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByExamAttempt(ExamAttempt examAttempt);

    Optional<StudentAnswer> findByExamAttemptAndQuestion(
            ExamAttempt examAttempt,
            com.exam.proctor.entity.Question question
    );
}

