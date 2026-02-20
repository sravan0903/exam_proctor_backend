package com.exam.proctor.service;


import com.exam.proctor.entity.ExamAttempt;
import com.exam.proctor.repository.ExamAttemptRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    public List<ExamAttempt> getAllResults() {
        return examAttemptRepository.findAll();
    }

    public List<ExamAttempt> getResultsByExam(Long examId) {
        return examAttemptRepository.findByExam(
                examAttemptRepository.findById(examId)
                        .orElseThrow(() -> new RuntimeException("Exam not found"))
                        .getExam()
        );
    }
}

