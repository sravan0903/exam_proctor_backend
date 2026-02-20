package com.exam.proctor.dto;

import java.time.LocalDateTime;

import lombok.*;
@Getter
@Setter
public class ExamResponseDTO {
	Long id;
    String examName;
    String branch;
    LocalDateTime deadline;
    int duration;
    int totalMarks;
    int violationLimit;
}
