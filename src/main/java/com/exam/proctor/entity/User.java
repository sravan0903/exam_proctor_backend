package com.exam.proctor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_role", columnList = "role")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ PostgreSQL compatible
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // 🔐 Store encrypted password (BCrypt ~60 chars)
    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 15)
    private String phone;

    @Column(length = 150)
    private String collegeName;

    @Column(length = 100)
    private String branch;

    @Column(length = 100)
    private String fatherName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;
}
