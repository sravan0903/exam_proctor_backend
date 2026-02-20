package com.exam.proctor.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String role;
    private String email;
}