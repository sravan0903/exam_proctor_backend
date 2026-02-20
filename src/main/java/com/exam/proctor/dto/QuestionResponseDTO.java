package com.exam.proctor.dto;

import lombok.*;

@Getter
@Setter
public class QuestionResponseDTO {
	 private Long id;
	    private String questionText;
	    private String optionA;
	    private String optionB;
	    private String optionC;
	    private String optionD;
	    private String correctAnswer;
}
