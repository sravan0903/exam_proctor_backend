package com.exam.proctor.dto;

import java.time.LocalDateTime;

import com.exam.proctor.entity.ExamStatus;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamSubmitResponseDTO {
	 private Long attemptId;
	    private Long examId;
	    private String examName;
	    private int score;
	    private ExamStatus status;
	    private LocalDateTime startTime;
	    private LocalDateTime endTime;
}
