package com.edutrack.edutrack.repository;

import com.edutrack.edutrack.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Page<Course> findAll(Pageable pageable);

    Optional<Course> findById(Long id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE c.id = :id")
    Optional<Course> findByIdWithInstructor(@Param("id") Long id);

    List<Course> findByInstructorId(Long instructorId);

    boolean existsByInstructorIdAndTitle(Long instructorId, String title);
}