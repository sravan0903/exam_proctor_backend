package com.exam.proctor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
    name = "exam_attempts",
    indexes = {
        @Index(name = "idx_exam_attempt_exam", columnList = "exam_id"),
        @Index(name = "idx_exam_attempt_student", columnList = "student_id"),
        @Index(name = "idx_exam_attempt_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Works perfectly in PostgreSQL
    private Long id;

    // 🔗 Exam reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "exam_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_exam_attempt_exam")
    )
    @JsonIgnore
    private Exam exam;

    // 👤 Student reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_exam_attempt_student")
    )
    @JsonIgnore
    private User student;

    // 🎯 Final score
    @Column(nullable = false)
    private int score;

    // 📌 Exam status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamStatus status;   // STARTED, SUBMITTED, AUTO_SUBMITTED

    // ⏱ Exam start time
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    // ⏹ Exam end time
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    // 🤖 Optional auto-submit reason
    @Column(length = 255)
    private String autoSubmitReason;
}
