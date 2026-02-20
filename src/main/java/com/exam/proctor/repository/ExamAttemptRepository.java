package com.exam.proctor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//import com.exam.proctor.dto.BranchScoreBarDTO;
//import com.exam.proctor.dto.BranchScorePieDTO;
import com.exam.proctor.entity.Exam;
import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.entity.User;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    Optional<ExamAttempt> findByExamAndStudent(Exam exam, User student);

    List<ExamAttempt> findByStudent(User student);

    List<ExamAttempt> findByExam(Exam exam);
    
    List<ExamAttempt> findAll();
    
    @Query("""
    	    SELECT ea
    	    FROM ExamAttempt ea
    	    JOIN FETCH ea.exam
    	    WHERE ea.student = :student
    	""")
    	List<ExamAttempt> findByStudentWithExam(@Param("student") User student);

	boolean existsByExam(Exam exam);
	
	 @Query("""
		        SELECT ea
		        FROM ExamAttempt ea
		        JOIN FETCH ea.exam
		        JOIN FETCH ea.student
		    """)
		    List<ExamAttempt> findAllWithExamAndStudent();
    


}
