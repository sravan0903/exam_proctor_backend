package com.exam.proctor.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileDTO {
	private Long id;
    private String name;
    private String email;
    private String role;
    private String branch;
    private String collegeName;
}
