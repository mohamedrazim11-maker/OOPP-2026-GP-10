-- =======================================================
-- Table Creation Script: Member 4 — Undergraduate, Timetable & Results
-- Faculty Management System | ICT2132 Mini Project
--
-- NOTE ON SHARED TABLES:
--   `courses`, `enrollments` and `marks` are logically shared with
--   Member 1 (courses) and Member 2 (marks). They are created here
--   with IF NOT EXISTS + minimal columns so Member 4's module can be
--   demoed independently while the group's other modules are still
--   being built. If Member 1/2 later create richer versions of these
--   tables, merge the column sets — do not just drop this script.
-- =======================================================

USE faculty_management_db;

-- ---------------------------------------------------------
-- 1. Courses (shared reference table — owned by Member 1)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    credits INT NOT NULL DEFAULT 3,
    theory_sessions INT NOT NULL DEFAULT 15,
    practical_sessions INT NOT NULL DEFAULT 0,
    semester_number INT NOT NULL DEFAULT 1,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2025/2026',
    department VARCHAR(100) DEFAULT 'ICT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 2. Enrollments — links a student to a course for a semester
--    (owned by Member 4: drives SGPA/CGPA grouping)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    semester_number INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    status ENUM('ONGOING', 'COMPLETED', 'REPEAT') DEFAULT 'ONGOING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT uq_enrollment UNIQUE (student_id, course_id, academic_year)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 3. Marks (shared reference table — owned by Member 2)
--    CA components are out of 100 each; CA% is the average of
--    the components that were actually entered.
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    quiz_marks DECIMAL(5,2) DEFAULT NULL,
    assignment_marks DECIMAL(5,2) DEFAULT NULL,
    mid_sem_marks DECIMAL(5,2) DEFAULT NULL,
    practical_marks DECIMAL(5,2) DEFAULT NULL,
    final_exam_marks DECIMAL(5,2) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT uq_marks UNIQUE (student_id, course_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 4. Timetable (owned by Member 4 — viewing side; Admin/Member 1 maintains)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS timetable (
    timetable_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    day_of_week ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    session_type ENUM('THEORY','PRACTICAL') NOT NULL DEFAULT 'THEORY',
    lecturer_name VARCHAR(100),
    venue VARCHAR(100),
    semester_number INT NOT NULL DEFAULT 1,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2025/2026',
    CONSTRAINT fk_timetable_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 5. Grades — one computed row per student/course (owned by Member 4)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    semester_number INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    ca_marks DECIMAL(5,2) NOT NULL,
    final_exam_marks DECIMAL(5,2) NOT NULL,
    final_total_marks DECIMAL(5,2) NOT NULL,
    ca_eligible TINYINT(1) NOT NULL DEFAULT 1,
    grade_letter VARCHAR(5) NOT NULL,
    grade_point DECIMAL(3,2) NOT NULL,
    computed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_grades_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_grades_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT uq_grade UNIQUE (student_id, course_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 6. Results — published SGPA summary per student per semester
--    (owned by Member 4)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    semester_number INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    sgpa DECIMAL(4,3) NOT NULL,
    credits_attempted INT NOT NULL,
    credits_completed INT NOT NULL,
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_result UNIQUE (student_id, semester_number, academic_year)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 7. Attendance â€” student-facing read-only summary per course
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance_records (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    theory_attended INT NOT NULL DEFAULT 0,
    theory_total INT NOT NULL DEFAULT 0,
    practical_attended INT NOT NULL DEFAULT 0,
    practical_total INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    CONSTRAINT uq_attendance UNIQUE (student_id, course_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 8. Medical submissions â€” students submit; status is read-only to them
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS medical_submissions (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    document_path VARCHAR(255),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_medical_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_medical_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL
) ENGINE=InnoDB;
