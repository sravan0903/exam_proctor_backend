package com.exam.proctor.dto;

import java.time.LocalDateTime;

import com.exam.proctor.entity.ViolationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViolationResponseDTO {
	 	private Long id;
	    private ViolationType violationType;
	    private LocalDateTime timestamp;

	    private Long examId;
	    private String examName;

	    private Long studentId;
	    private String studentName;
}
