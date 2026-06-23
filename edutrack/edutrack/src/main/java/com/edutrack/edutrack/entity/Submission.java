package com.edutrack.edutrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "assignment_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    private Double grade;

    @Column(length = 500)
    private String feedback;

    @Column(name = "similarity_score")
    private Double similarityScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;
}
