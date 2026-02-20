package com.exam.proctor.dto;

import java.time.LocalDateTime;

import com.exam.proctor.entity.ExamStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class ExamAttemptResponseDTO {
	private Long id;

    private Long examId;
    private String examName;

    private Long studentId;
    private String studentName;
    private String branch;        // ⭐ derived from User

    private Integer score;
    private Integer totalMarks;

    private ExamStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
