package com.exam.proctor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "student_answers",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_exam_attempt_question",
            columnNames = {"exam_attempt_id", "question_id"}
        )
    },
    indexes = {
        @Index(name = "idx_student_answer_attempt", columnList = "exam_attempt_id"),
        @Index(name = "idx_student_answer_question", columnList = "question_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ PostgreSQL compatible
    private Long id;

    // 🔗 Exam Attempt reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "exam_attempt_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_student_answer_attempt")
    )
    private ExamAttempt examAttempt;

    // 🔗 Question reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "question_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_student_answer_question")
    )
    private Question question;

    // 🎯 Selected answer
    @Column(nullable = false, length = 1)
    private String selectedAnswer; // A, B, C, D
}
