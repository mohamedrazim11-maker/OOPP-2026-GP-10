-- =======================================================
-- Table Creation Script: Roles & Users
-- Faculty Management System
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

-- 3. Courses Table
CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    credit_value INT NOT NULL DEFAULT 2,
    theory_hours INT DEFAULT 30,
    practical_hours INT DEFAULT 30,
    department VARCHAR(100) NOT NULL,
    semester VARCHAR(50) DEFAULT 'Semester 2',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =======================================================
-- Member 3: Attendance & Medical Records Module
-- =======================================================

-- 4. Table of attendance sessions (Theory or Practical sessions)
CREATE TABLE IF NOT EXISTS attendance_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id VARCHAR(20) NOT NULL,
    session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
    session_number INT DEFAULT 1,
    session_date DATE NOT NULL,
    hours DECIMAL(3,1) DEFAULT 2.0,
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 5. Attendance record table for each student
CREATE TABLE IF NOT EXISTS attendance_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    student_id INT NOT NULL,
    status ENUM('PRESENT', 'ABSENT') NOT NULL DEFAULT 'ABSENT',
    FOREIGN KEY (session_id) REFERENCES attendance_sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_session_student (session_id, student_id)
) ENGINE=InnoDB;

-- 6. Medical certificate record table
CREATE TABLE IF NOT EXISTS medical_records (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id VARCHAR(20) NOT NULL,
    session_id INT NULL,
    medical_date DATE NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES attendance_sessions(session_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =======================================================
-- Member 2: Lecturer & Academic Management Module
-- =======================================================

-- 7. Course Materials Table
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

-- 8. Course Lecturers Mapping Table (Which lecturer teaches which course)
CREATE TABLE IF NOT EXISTS course_lecturers (
      course_id INT NOT NULL,
      lecturer_id INT NOT NULL,
      PRIMARY KEY (course_id, lecturer_id),
      CONSTRAINT fk_cl_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT fk_cl_lecturer FOREIGN KEY (lecturer_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 9. Evaluations Table (Quizzes, Assessments, Mid-term, Practical, Final)
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

-- 10. Marks Table (Marks obtained per student per evaluation, mandatory out of 100)
CREATE TABLE IF NOT EXISTS marks (
      mark_id INT AUTO_INCREMENT PRIMARY KEY,
      student_id INT NOT NULL, -- user_id of the student
      evaluation_id INT NOT NULL,
      marks_obtained DOUBLE NOT NULL,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT fk_marks_evaluation FOREIGN KEY (evaluation_id) REFERENCES evaluations(evaluation_id) ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT check_mark_range CHECK (marks_obtained >= 0 AND marks_obtained <= 100),
      UNIQUE KEY unique_student_evaluation (student_id, evaluation_id)
) ENGINE=InnoDB;

-- 11. CA Eligibility Table
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
