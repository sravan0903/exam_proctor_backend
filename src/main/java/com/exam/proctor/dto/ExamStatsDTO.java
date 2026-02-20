package com.exam.proctor.dto;

import lombok.*;

@Getter
@Setter 
@AllArgsConstructor
public class ExamStatsDTO {
	private long totalExams;
    private long activeExams;
    private long totalQuestions;
}
