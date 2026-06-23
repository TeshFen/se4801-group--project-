package com.edutrack.edutrack.repository;

import com.edutrack.edutrack.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    Page<Submission> findByAssignmentId(Long assignmentId, Pageable pageable);

    Page<Submission> findByStudentId(Long studentId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Submission s SET s.grade = :grade, s.feedback = :feedback WHERE s.id = :submissionId")
    void updateGrade(@Param("submissionId") Long submissionId,
                     @Param("grade") Double grade,
                     @Param("feedback") String feedback);

    boolean existsByAssignmentId(Long assignmentId);
}
