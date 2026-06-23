package com.edutrack.edutrack.repository;

import com.edutrack.edutrack.entity.Enrollment;
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
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);

    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);

    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, Enrollment.Status status);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.studentId = :studentId AND e.status = 'ACTIVE'")
    long countActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE Enrollment e SET e.status = :status WHERE e.id = :enrollmentId")
    void updateStatus(@Param("enrollmentId") Long enrollmentId, @Param("status") Enrollment.Status status);

    @Modifying
    @Transactional
    void deleteByCourseId(Long courseId);
}
