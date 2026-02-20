package com.exam.proctor.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
public class ExamDTO {

    private String examName;
    private String branch;
    private int duration;
    private int totalMarks;
    private LocalDateTime deadline;
    private int violationLimit;
}
