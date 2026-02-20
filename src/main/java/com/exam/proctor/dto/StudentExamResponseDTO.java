package com.exam.proctor.dto;

import lombok.*;

@Getter
@Setter
public class StudentExamResponseDTO {
	private Long id;
    private String examName;
    private String branch;
    private Integer duration;
    private Boolean active;
    private Integer totalMarks;
}
