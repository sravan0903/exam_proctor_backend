package com.exam.proctor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.exam.proctor.entity.Exam;
import com.exam.proctor.entity.User;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByBranch(String branch);

    List<Exam> findByExaminer(User examiner);
    
    List<Exam> findByExaminer_Email(String email);

    @Query("""
            SELECT DISTINCT e
            FROM Exam e
            LEFT JOIN FETCH e.questions
            WHERE e.id = :examId
        """)
        Optional<Exam> findByIdWithQuestions(@Param("examId") Long examId);
}