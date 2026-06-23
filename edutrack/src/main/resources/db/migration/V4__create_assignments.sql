CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    deadline TIMESTAMP NOT NULL,
    max_score INTEGER NOT NULL CHECK (max_score BETWEEN 1 AND 100),
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE
);