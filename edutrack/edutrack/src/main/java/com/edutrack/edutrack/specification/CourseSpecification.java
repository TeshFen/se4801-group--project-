package com.edutrack.edutrack.specification;

import com.edutrack.edutrack.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {

    public static Specification<Course> searchCourses(String keyword, Long instructorId, LocalDate minStartDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
                predicates.add(criteriaBuilder.or(titleMatch, descMatch));
            }

            if (instructorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("instructorId"), instructorId));
            }

            if (minStartDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), minStartDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}