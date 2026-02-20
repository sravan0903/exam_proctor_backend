package com.exam.proctor.dto;

import com.exam.proctor.entity.Role;

import lombok.Builder;
import lombok.*;

@Getter
@Setter
@Builder
public class UserResponseDTO {
	private Long id;
    private String name;
    private String email;
    private String phone;
    private String collegeName;
    private String branch;
    private String fatherName;
    private Role role;
    private boolean active;
}
