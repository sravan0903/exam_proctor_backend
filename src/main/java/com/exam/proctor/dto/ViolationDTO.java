package com.exam.proctor.dto;

import com.exam.proctor.entity.ViolationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViolationDTO {

    private Long examId;
    private ViolationType violationType;
}