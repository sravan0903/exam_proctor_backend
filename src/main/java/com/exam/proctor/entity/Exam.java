package com.exam.proctor.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "exams") // 🔥 lowercase table name (important for PostgreSQL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ works in PostgreSQL
    private Long id;

    @Column(nullable = false)
    private String examName;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private int totalMarks;

    // 🔐 Proctoring
    @Column(nullable = false)
    private int violationLimit;

    @Column(nullable = false)
    private boolean active = true;

    // ⏰ Deadline
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examiner_id", nullable = false)
    private User examiner;

    @OneToMany(
        mappedBy = "exam",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Question> questions;
}
