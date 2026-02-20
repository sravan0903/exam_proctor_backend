package com.exam.proctor.dto;

import lombok.*;

@Getter
@Setter
public class StudentAnswerDTO {

    private Long examId;
    private Long questionId;
    private String selectedAnswer;
}
