package com.exam.proctor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "violations",
    indexes = {
        @Index(name = "idx_violation_attempt", columnList = "exam_attempt_id"),
        @Index(name = "idx_violation_type", columnList = "violation_type"),
        @Index(name = "idx_violation_timestamp", columnList = "timestamp")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ PostgreSQL compatible
    private Long id;

    // 🔗 Exam Attempt reference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "exam_attempt_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_violation_attempt")
    )
    private ExamAttempt examAttempt;

    // 🚨 Violation type (AI detected)
    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type", nullable = false, length = 50)
    private ViolationType violationType;

    // ⏱ Timestamp of violation
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
