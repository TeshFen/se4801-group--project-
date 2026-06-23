package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.request.CreateAssignmentRequest;
import com.edutrack.edutrack.dto.request.UpdateAssignmentRequest;
import com.edutrack.edutrack.dto.response.AssignmentDTO;
import com.edutrack.edutrack.entity.Assignment;
import com.edutrack.edutrack.mapper.AssignmentMapper;
import com.edutrack.edutrack.repository.AssignmentRepository;
import com.edutrack.edutrack.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentMapper assignmentMapper;

    @Transactional(readOnly = true)
    public Page<AssignmentDTO> getAssignmentsByCourse(Long courseId, Pageable pageable) {
        return assignmentRepository.findByCourseId(courseId, pageable)
                .map(assignmentMapper::toDTO);
    }

    @Transactional
    public AssignmentDTO createAssignment(Long courseId, CreateAssignmentRequest request, Long instructorId) {
        // verify course exists and belongs to instructor
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new IllegalStateException("You can only add assignments to your own courses");
        }
        Assignment assignment = assignmentMapper.toEntity(request, courseId);
        Assignment saved = assignmentRepository.save(assignment);
        return assignmentMapper.toDTO(saved);
    }

    @Transactional
    public AssignmentDTO updateAssignment(Long id, UpdateAssignmentRequest request, Long instructorId) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        var course = courseRepository.findById(assignment.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new IllegalStateException("You can only update assignments from your own courses");
        }
        if (request.getTitle() != null) assignment.setTitle(request.getTitle());
        if (request.getDescription() != null) assignment.setDescription(request.getDescription());
        if (request.getDeadline() != null) assignment.setDeadline(request.getDeadline());
        if (request.getMaxScore() != null) assignment.setMaxScore(request.getMaxScore());
        Assignment updated = assignmentRepository.save(assignment);
        return assignmentMapper.toDTO(updated);
    }

    @Transactional
    public void deleteAssignment(Long id, Long instructorId) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        var course = courseRepository.findById(assignment.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new IllegalStateException("You can only delete assignments from your own courses");
        }
        assignmentRepository.deleteById(id);
    }
}
