package com.exam.proctor.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.entity.Violation;
import com.exam.proctor.entity.ViolationType;

public interface ViolationRepository extends JpaRepository<Violation, Long> {

    // 🔢 Count violations for a specific attempt (used for auto-submit)
    long countByExamAttempt(ExamAttempt attempt);

    // 📋 Get all violations for an attempt
    List<Violation> findByExamAttempt(ExamAttempt attempt);

    // 📊 Admin: get all violations for an exam (all students)
    @Query("""
        SELECT v
        FROM Violation v
        JOIN FETCH v.examAttempt ea
        JOIN FETCH ea.student
        WHERE ea.exam.id = :examId
    """)
    List<Violation> findByExamId(Long examId);
    
    boolean existsByExamAttemptAndViolationTypeAndTimestampAfter(
    	    ExamAttempt attempt,
    	    ViolationType violationType,
    	    LocalDateTime after
    	);
    
    @Query("""
    	    SELECT v
    	    FROM Violation v
    	    JOIN FETCH v.examAttempt ea
    	    JOIN FETCH ea.exam
    	    JOIN FETCH ea.student
    	""")
    	List<Violation> findAllWithDetails();


}
