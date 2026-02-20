package com.exam.proctor.dto;

import lombok.*;

@Getter
@Setter
public class QuestionDTO {

	private Long questionId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
}
