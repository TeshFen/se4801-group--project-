package com.edutrack.edutrack.repository;

import com.edutrack.edutrack.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Page<Assignment> findByCourseId(Long courseId, Pageable pageable);

    List<Assignment> findByCourseId(Long courseId);

    @Query("SELECT a FROM Assignment a WHERE a.deadline < :now AND a.deadline > :oneHourAgo")
    List<Assignment> findAssignmentsDueWithinLastHour(@Param("now") LocalDateTime now,
                                                      @Param("oneHourAgo") LocalDateTime oneHourAgo);

    @Modifying
    @Transactional
    void deleteByCourseId(Long courseId);

    boolean existsByCourseId(Long courseId);
}
