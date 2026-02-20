package com.exam.proctor.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
public class StudentExamPlayDTO {
	 	private Long id;
	    private String examName;
	    private int duration;
	    private int violationLimit;
	    private List<QuestionDTO> questions;
}
