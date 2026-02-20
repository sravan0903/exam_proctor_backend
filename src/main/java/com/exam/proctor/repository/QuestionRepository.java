package com.exam.proctor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exam.proctor.entity.Exam;
import com.exam.proctor.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByExam(Exam exam);
    
    List<Question> findByExamId(Long examId);
    
    long countByExamIdIn(List<Long> examIds);
}
