CREATE TABLE submissions (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assignment_id BIGINT NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    file_url VARCHAR(500),
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    grade DOUBLE PRECISION CHECK (grade BETWEEN 0 AND 100),
    feedback VARCHAR(500),
    similarity_score DOUBLE PRECISION CHECK (similarity_score BETWEEN 0 AND 1),
    CONSTRAINT uk_submission_student_assignment UNIQUE (student_id, assignment_id)
);