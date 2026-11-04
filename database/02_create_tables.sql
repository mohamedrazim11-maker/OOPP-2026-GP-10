-- =======================================================
-- Table Creation Script: Unified Schema
-- Faculty Management System | University of Ruhuna
-- =======================================================

USE faculty_management_db;

-- 1. Roles Table
CREATE TABLE IF NOT EXISTS roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role_id INT NOT NULL,
    contact_no VARCHAR(20),
    profile_pic VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 3. Courses Table (Unified Member 1 & Member 4 attributes)
CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    credit_value INT NOT NULL DEFAULT 2,
    credits INT NOT NULL DEFAULT 3,
    theory_hours INT DEFAULT 30,
    practical_hours INT DEFAULT 30,
    theory_sessions INT NOT NULL DEFAULT 15,
    practical_sessions INT NOT NULL DEFAULT 0,
    department VARCHAR(100) NOT NULL,
    semester VARCHAR(50) DEFAULT 'Semester 2',
    semester_number INT NOT NULL DEFAULT 1,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2025/2026',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =======================================================
-- Member 2: Lecturer & Academic Management Module
-- =======================================================

CREATE TABLE IF NOT EXISTS course_materials (
    material_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    uploaded_by INT NOT NULL, -- lecturer user_id
    title VARCHAR(150) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_materials_lecturer FOREIGN KEY (uploaded_by) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course_lecturers (
    course_id INT NOT NULL,
    lecturer_id INT NOT NULL,
    PRIMARY KEY (course_id, lecturer_id),
    CONSTRAINT fk_cl_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cl_lecturer FOREIGN KEY (lecturer_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS evaluations (
    evaluation_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    title VARCHAR(100) NOT NULL, -- e.g., 'Quiz 1', 'Mid Semester Exam', 'Final Practical'
    type ENUM('QUIZ', 'ASSESSMENT', 'MID_TERM', 'PRACTICAL', 'FINAL') NOT NULL,
    weightage DOUBLE NOT NULL,   -- Percentage weightage (e.g., 10 for 10%)
    max_marks DOUBLE DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evaluations_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Unified Marks Table (Supports Member 2 evaluation marks and Member 4 component marks)
CREATE TABLE IF NOT EXISTS marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL, -- user_id of the student
    course_id INT NULL,
    evaluation_id INT NULL,
    marks_obtained DOUBLE NULL,
    quiz_marks DECIMAL(5,2) DEFAULT NULL,
    assignment_marks DECIMAL(5,2) DEFAULT NULL,
    mid_sem_marks DECIMAL(5,2) DEFAULT NULL,
    practical_marks DECIMAL(5,2) DEFAULT NULL,
    final_exam_marks DECIMAL(5,2) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_marks_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_marks_evaluation FOREIGN KEY (evaluation_id) REFERENCES evaluations(evaluation_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT check_mark_range CHECK (marks_obtained IS NULL OR (marks_obtained >= 0 AND marks_obtained <= 100)),
    UNIQUE KEY unique_student_evaluation (student_id, evaluation_id),
    UNIQUE KEY unique_student_course_mark (student_id, course_id, evaluation_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS eligibility (
    eligibility_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    ca_percentage DOUBLE NOT NULL,
    is_eligible BOOLEAN NOT NULL, -- TRUE if ca_percentage >= 40.0
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_eligibility_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_eligibility_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY unique_student_course_eligibility (student_id, course_id)
) ENGINE=InnoDB;

-- =======================================================
-- Member 3: Technical Officer & Attendance/Medical Module
-- =======================================================

CREATE TABLE IF NOT EXISTS attendance_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id VARCHAR(20) NOT NULL,
    session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
    session_number INT DEFAULT 1,
    session_date DATE NOT NULL,
    hours DECIMAL(3,1) DEFAULT 2.0,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_sess_user FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Unified Attendance Records Table (Supports Member 3 session records and Member 4 course summaries)
CREATE TABLE IF NOT EXISTS attendance_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    attendance_id INT NULL,
    session_id INT NULL,
    student_id INT NOT NULL,
    course_id INT NULL,
    status ENUM('PRESENT', 'ABSENT') DEFAULT 'PRESENT',
    theory_attended INT NOT NULL DEFAULT 0,
    theory_total INT NOT NULL DEFAULT 0,
    practical_attended INT NOT NULL DEFAULT 0,
    practical_total INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_att_rec_session FOREIGN KEY (session_id) REFERENCES attendance_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT fk_att_rec_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_att_rec_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_session_student (session_id, student_id),
    UNIQUE KEY unique_course_student (student_id, course_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medical_records (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id VARCHAR(20) NOT NULL,
    session_id INT NULL,
    medical_date DATE NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    document_path VARCHAR(255) NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_med_rec_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_med_rec_session FOREIGN KEY (session_id) REFERENCES attendance_sessions(session_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =======================================================
-- Member 4: Undergraduate, Enrollments, Timetable, Grades, Results & Medical Submissions
-- =======================================================

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
    CONSTRAINT uq_enrollment UNIQUE (student_id, course_id, semester_number, academic_year)
) ENGINE=InnoDB;

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
    CONSTRAINT uq_grade UNIQUE (student_id, course_id, semester_number, academic_year)
) ENGINE=InnoDB;

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
    CONSTRAINT fk_medical_sub_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_medical_sub_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =======================================================
-- Notices & Announcements (Used across all modules)
-- =======================================================

CREATE TABLE IF NOT EXISTS notices (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category ENUM('ACADEMIC', 'EXAMINATION', 'TIMETABLE', 'GENERAL', 'EVENT') DEFAULT 'GENERAL',
    priority ENUM('NORMAL', 'URGENT', 'INFO') DEFAULT 'NORMAL',
    posted_by VARCHAR(100) NOT NULL,
    posted_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
