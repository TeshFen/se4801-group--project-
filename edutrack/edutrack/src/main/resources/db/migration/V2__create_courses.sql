CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    instructor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_course_dates CHECK (end_date > start_date)
);