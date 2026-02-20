package com.exam.proctor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "questions",
    indexes = {
        @Index(name = "idx_question_exam", columnList = "exam_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ PostgreSQL compatible
    private Long id;

    // 🔗 Exam reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "exam_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_question_exam")
    )
    private Exam exam;

    // 📝 Question text (can be long)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    @Column(nullable = false)
    private String optionC;

    @Column(nullable = false)
    private String optionD;

    @Column(nullable = false, length = 1)
    private String correctAnswer;  // A, B, C, D
}
