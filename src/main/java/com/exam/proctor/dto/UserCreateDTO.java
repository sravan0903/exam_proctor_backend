package com.exam.proctor.dto;

import com.exam.proctor.entity.Role;
import lombok.*;

@Getter
@Setter
public class UserCreateDTO {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String collegeName;
    private String branch;
    private String fatherName;
    private Role role;
}
