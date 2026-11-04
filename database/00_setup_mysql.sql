-- ==============================================================================
-- Faculty Management System (FMS) | University of Ruhuna - Faculty of Technology
-- MySQL / XAMPP Master Database Script (DDL & DML)
-- File: database/text/mysql.txt (also database/00_setup_mysql.sql)
-- Supported Environments: XAMPP phpMyAdmin, MySQL Workbench, MySQL 8.x, MariaDB
-- ==============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- ------------------------------------------------------------------------------
-- 0. Database Creation
-- ------------------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS aculty_management_db
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE aculty_management_db;

-- ==============================================================================
-- 1. TABLE DEFINITIONS (DDL)
-- ==============================================================================
-- =======================================================
-- Table Creation Script: Unified Schema
-- Faculty Management System | University of Ruhuna
-- =======================================================

-- DDL Tables Definition

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


-- ==============================================================================
-- 2. SEED DATA (DML)
-- ==============================================================================
-- ------------------------------------------------------------------------------
-- File: database/03_insert_roles.sql
-- ------------------------------------------------------------------------------
-- =======================================================
-- Seed Script: User Roles
-- Faculty Management System
-- =======================================================

-- database/03_insert_roles.sql Seed Data

INSERT INTO roles (role_id, role_name, description) VALUES
(1, 'ADMIN', 'System Administrator with full management privileges'),
(2, 'LECTURER', 'Academic staff handling courses, marks, materials, and student progress'),
(3, 'TECHNICAL_OFFICER', 'Technical Officer handling attendance, lab sessions, and medical submissions'),
(4, 'UNDERGRADUATE', 'Enrolled student accessing attendance, courses, grades, notices, and timetables')
ON DUPLICATE KEY UPDATE 
    role_name = VALUES(role_name),
    description = VALUES(description);


-- ------------------------------------------------------------------------------
-- File: database/04_insert_users.sql
-- ------------------------------------------------------------------------------
-- =======================================================
-- Seed Script: User Accounts
-- Faculty Management System
-- =======================================================

-- database/04_insert_users.sql Seed Data

INSERT INTO users (user_id, username, password, email, first_name, last_name, role_id, contact_no, status) VALUES
-- 1 Admin
(1, 'admin', 'admin123', 'admin@fot.ruh.ac.lk', 'Admin', 'User', 1, '0711234567', 'ACTIVE'),

-- 5 Lecturers
(2, 'lec_kamal', 'lec123', 'kamal@fot.ruh.ac.lk', 'Kamal', 'Perera', 2, '0771122334', 'ACTIVE'),
(3, 'lec_nimal', 'lec123', 'nimal@fot.ruh.ac.lk', 'Nimal', 'Silva', 2, '0772233445', 'ACTIVE'),
(4, 'lec_sunil', 'lec123', 'sunil@fot.ruh.ac.lk', 'Sunil', 'Fernando', 2, '0773344556', 'ACTIVE'),
(5, 'lec_amara', 'lec123', 'amara@fot.ruh.ac.lk', 'Amara', 'Jayasinghe', 2, '0774455667', 'ACTIVE'),
(6, 'lec_champa', 'lec123', 'champa@fot.ruh.ac.lk', 'Champa', 'Wickramasinghe', 2, '0775566778', 'ACTIVE'),

-- 4 Technical Officers
(7, 'to_bandara', 'to123', 'bandara@fot.ruh.ac.lk', 'Bandara', 'Herath', 3, '0761122334', 'ACTIVE'),
(8, 'to_sarath', 'to123', 'sarath@fot.ruh.ac.lk', 'Sarath', 'Kumara', 3, '0762233445', 'ACTIVE'),
(9, 'to_anura', 'to123', 'anura@fot.ruh.ac.lk', 'Anura', 'Dissanayake', 3, '0763344556', 'ACTIVE'),
(10, 'to_kasun', 'to123', 'kasun@fot.ruh.ac.lk', 'Kasun', 'Gunawardena', 3, '0764455667', 'ACTIVE'),

-- Undergraduates (Students)
(11, 'tg2021001', 'std123', 'tg2021001@fot.ruh.ac.lk', 'Kasun', 'Kalhara', 4, '0701122334', 'ACTIVE'),
(12, 'tg2021002', 'std123', 'tg2021002@fot.ruh.ac.lk', 'Nuwan', 'Pradeep', 4, '0702233445', 'ACTIVE'),
(13, 'tg2021003', 'std123', 'tg2021003@fot.ruh.ac.lk', 'Dinuka', 'Madushan', 4, '0703344556', 'ACTIVE'),
(14, 'tg2021004', 'std123', 'tg2021004@fot.ruh.ac.lk', 'Sajith', 'Premadasa', 4, '0704455667', 'ACTIVE'),
(15, 'tg2021005', 'std123', 'tg2021005@fot.ruh.ac.lk', 'Ishara', 'Sandaruwan', 4, '0705566778', 'ACTIVE')
ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    email = VALUES(email),
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    role_id = VALUES(role_id),
    contact_no = VALUES(contact_no),
    status = VALUES(status);


-- ------------------------------------------------------------------------------
-- File: database/05_insert_courses.sql
-- ------------------------------------------------------------------------------
-- =======================================================
-- Seed Data Script: Courses
-- Faculty Management System (Faculty of Technology)
-- =======================================================

-- database/05_insert_courses.sql Seed Data

INSERT INTO courses (course_code, course_name, credit_value, theory_hours, practical_hours, department, semester, description) VALUES
('ICT2112', 'Data Structures and Algorithms', 2, 30, 30, 'Department of Information & Communication Technology', 'Semester 2', 'Core algorithms, abstract data types, trees, graphs, and complexity analysis'),
('ICT2123', 'Database Management Systems', 3, 30, 60, 'Department of Information & Communication Technology', 'Semester 2', 'Relational data modeling, SQL programming, normalization, and indexing'),
('ICT2132', 'Object Oriented Programming Practicum', 2, 0, 60, 'Department of Information & Communication Technology', 'Semester 2', 'Comprehensive hands-on OOP software development using Java Swing and MySQL'),
('BST2113', 'Bioprocess Technology', 3, 30, 60, 'Department of Biosystems Technology', 'Semester 2', 'Bioreactor engineering, downstream processing, and industrial applications'),
('ENG2113', 'Engineering Mechanics', 3, 30, 60, 'Department of Engineering Technology', 'Semester 2', 'Engineering statics, dynamics, stress analysis, and structural mechanics')
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);


-- ------------------------------------------------------------------------------
-- File: database/07_insert_member2_data.sql
-- ------------------------------------------------------------------------------
-- =======================================================
-- Seed Script: Member 2 (Lecturer & Academic Management)
-- Handles: course_materials, course_lecturers, evaluations, marks, eligibility
-- Faculty Management System
-- =======================================================

-- database/07_insert_member2_data.sql Seed Data

-- 1. Course Lecturers Mapping (Assign courses to lecturers)
-- Lecturer user_ids: 2 (Kamal), 3 (Nimal), 4 (Sunil), 5 (Amara), 6 (Champa)
-- Course IDs: 1 (ICT2112), 2 (ICT2123), 3 (ICT2132), 4 (BST2113), 5 (ENG2113)
INSERT INTO course_lecturers (course_id, lecturer_id) VALUES
(1, 2), -- Kamal teaches ICT2112 (Data Structures)
(2, 2), -- Kamal teaches ICT2123 (Database Systems)
(3, 3), -- Nimal teaches ICT2132 (OOP Practicum)
(4, 4), -- Sunil teaches BST2113 (Bioprocess Technology)
(5, 5)  -- Amara teaches ENG2113 (Engineering Mechanics)
ON DUPLICATE KEY UPDATE course_id = VALUES(course_id);

-- 2. Course Materials (Upload lecture notes, tutorial sheets, assignment specs)
INSERT INTO course_materials (material_id, course_id, uploaded_by, title, file_path, uploaded_at) VALUES
(1, 1, 2, 'Lecture 01 - Algorithm Analysis & Big-O Notation', 'resources/course_materials/ICT2112_Lecture01.pdf', NOW()),
(2, 1, 2, 'Tutorial 01 - Linked Lists & Stacks Problem Set', 'resources/course_materials/ICT2112_Tutorial01.pdf', NOW()),
(3, 2, 2, 'Lecture 01 - Relational Algebra & Normalization', 'resources/course_materials/ICT2123_Lecture01.pdf', NOW()),
(4, 2, 2, 'Lab Sheet 01 - Complex SQL Queries & Joins', 'resources/course_materials/ICT2123_Lab01.pdf', NOW()),
(5, 3, 3, 'Practicum Guide - Swing GUI & MVC Architecture', 'resources/course_materials/ICT2132_SwingGuide.pdf', NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), file_path = VALUES(file_path);

-- 3. Evaluations Table (Quizzes, Assessments, Mid-term, Practical, Final)
INSERT INTO evaluations (evaluation_id, course_id, title, type, weightage, max_marks) VALUES
(1, 1, 'Quiz 1 - Stacks and Queues', 'QUIZ', 10.0, 100.0),
(2, 1, 'Continuous Assessment 1', 'ASSESSMENT', 15.0, 100.0),
(3, 1, 'Mid-Semester Theory Examination', 'MID_TERM', 25.0, 100.0),
(4, 1, 'Practical Lab Test', 'PRACTICAL', 20.0, 100.0),
(5, 1, 'End-Semester Final Examination', 'FINAL', 30.0, 100.0),

(6, 2, 'Quiz 1 - SQL DDL and DML', 'QUIZ', 10.0, 100.0),
(7, 2, 'Database Project Assessment', 'ASSESSMENT', 20.0, 100.0),
(8, 2, 'Mid-Semester Exam', 'MID_TERM', 20.0, 100.0),
(9, 2, 'Final Written Examination', 'FINAL', 50.0, 100.0),

(10, 3, 'OOP Quiz - Inheritance & Polymorphism', 'QUIZ', 10.0, 100.0),
(11, 3, 'Mini Project Milestone 1', 'ASSESSMENT', 20.0, 100.0),
(12, 3, 'Mid Practicum Exam', 'MID_TERM', 20.0, 100.0),
(13, 3, 'Final Project & Viva', 'FINAL', 50.0, 100.0)
ON DUPLICATE KEY UPDATE title = VALUES(title), weightage = VALUES(weightage);

-- 4. Marks Table (All marks strictly out of 100)
-- Student user_ids: 11 (Kasun), 12 (Nuwan), 13 (Dinuka), 14 (Sajith), 15 (Ishara)
INSERT INTO marks (mark_id, student_id, evaluation_id, marks_obtained) VALUES
-- Kasun (Student 11) - Highly Eligible
(1, 11, 1, 85.0),
(2, 11, 2, 78.0),
(3, 11, 3, 72.0),
(4, 11, 4, 80.0),
(5, 11, 5, 88.0),

-- Nuwan (Student 12) - Eligible
(6, 12, 1, 65.0),
(7, 12, 2, 60.0),
(8, 12, 3, 58.0),
(9, 12, 4, 70.0),
(10, 12, 5, 62.0),

-- Dinuka (Student 13) - Marginal Eligible
(11, 13, 1, 45.0),
(12, 13, 2, 42.0),
(13, 13, 3, 40.0),
(14, 13, 4, 48.0),
(15, 13, 5, 50.0),

-- Sajith (Student 14) - Not Eligible (CA < 40%)
(16, 14, 1, 28.0),
(17, 14, 2, 32.0),
(18, 14, 3, 25.0),
(19, 14, 4, 30.0),
(20, 14, 5, 35.0),

-- Ishara (Student 15) - Eligible
(21, 15, 1, 92.0),
(22, 15, 2, 88.0),
(23, 15, 3, 85.0),
(24, 15, 4, 90.0),
(25, 15, 5, 86.0)
ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained);

-- 5. CA Eligibility Table (CA >= 40% threshold demonstrated)
INSERT INTO eligibility (eligibility_id, student_id, course_id, ca_percentage, is_eligible) VALUES
(1, 11, 1, 78.75, TRUE),   -- Kasun: Eligible
(2, 12, 1, 63.25, TRUE),   -- Nuwan: Eligible
(3, 13, 1, 43.75, TRUE),   -- Dinuka: Eligible
(4, 14, 1, 28.75, FALSE),  -- Sajith: Not Eligible (CA < 40%)
(5, 15, 1, 88.75, TRUE),   -- Ishara: Eligible

(6, 11, 2, 82.00, TRUE),   -- Kasun: Eligible for DB
(7, 12, 2, 67.50, TRUE),   -- Nuwan: Eligible for DB
(8, 14, 2, 34.00, FALSE)   -- Sajith: Not Eligible for DB
ON DUPLICATE KEY UPDATE ca_percentage = VALUES(ca_percentage), is_eligible = VALUES(is_eligible);


-- ------------------------------------------------------------------------------
-- File: database/M4_02_seed_data.sql
-- ------------------------------------------------------------------------------
-- =======================================================
-- Seed Script: Member 4 â€” Courses, Enrollments, Marks, Timetable
-- Faculty Management System | ICT2132 Mini Project
-- Demo dataset: 20 undergraduates across 2 semesters (10 courses)
-- =======================================================

-- database/M4_02_seed_data.sql Seed Data

-- ---------------------------------------------------------
-- Supplementary undergraduate accounts (to reach 20+ students)
-- Coordinate with Member 1's final users seed script; safe to
-- re-run thanks to ON DUPLICATE KEY UPDATE.
-- ---------------------------------------------------------
INSERT INTO users (user_id, username, password, email, first_name, last_name, role_id, contact_no, status) VALUES
(16, 'tg2021006', 'std123', 'tg2021006@fot.ruh.ac.lk', 'Tharindu', 'Bandara', 4, '0795822412', 'ACTIVE'),
(17, 'tg2021007', 'std123', 'tg2021007@fot.ruh.ac.lk', 'Chamodi', 'Weerasinghe', 4, '0724942603', 'ACTIVE'),
(18, 'tg2021008', 'std123', 'tg2021008@fot.ruh.ac.lk', 'Yasas', 'Jayawardena', 4, '0713356886', 'ACTIVE'),
(19, 'tg2021009', 'std123', 'tg2021009@fot.ruh.ac.lk', 'Hiruni', 'Rathnayake', 4, '0746913810', 'ACTIVE'),
(20, 'tg2021010', 'std123', 'tg2021010@fot.ruh.ac.lk', 'Dulaj', 'Abeywardena', 4, '0742868828', 'ACTIVE'),
(21, 'tg2021011', 'std123', 'tg2021011@fot.ruh.ac.lk', 'Sanduni', 'Wijesekara', 4, '0739958838', 'ACTIVE'),
(22, 'tg2021012', 'std123', 'tg2021012@fot.ruh.ac.lk', 'Ravindu', 'Gamage', 4, '0728728463', 'ACTIVE'),
(23, 'tg2021013', 'std123', 'tg2021013@fot.ruh.ac.lk', 'Vihanga', 'Senanayake', 4, '0723756669', 'ACTIVE'),
(24, 'tg2021014', 'std123', 'tg2021014@fot.ruh.ac.lk', 'Nethmi', 'Kularatne', 4, '0783197857', 'ACTIVE'),
(25, 'tg2021015', 'std123', 'tg2021015@fot.ruh.ac.lk', 'Oshadi', 'Athapattu', 4, '0721668732', 'ACTIVE'),
(26, 'tg2021016', 'std123', 'tg2021016@fot.ruh.ac.lk', 'Malith', 'Bandara', 4, '0789254563', 'ACTIVE'),
(27, 'tg2021017', 'std123', 'tg2021017@fot.ruh.ac.lk', 'Piumi', 'Weerasinghe', 4, '0766629388', 'ACTIVE'),
(28, 'tg2021018', 'std123', 'tg2021018@fot.ruh.ac.lk', 'Isuru', 'Jayawardena', 4, '0714265799', 'ACTIVE'),
(29, 'tg2021019', 'std123', 'tg2021019@fot.ruh.ac.lk', 'Kavindu', 'Rathnayake', 4, '0713999315', 'ACTIVE'),
(30, 'tg2021020', 'std123', 'tg2021020@fot.ruh.ac.lk', 'Sathmini', 'Abeywardena', 4, '0722575562', 'ACTIVE')
ON DUPLICATE KEY UPDATE password = VALUES(password), email = VALUES(email), first_name = VALUES(first_name), last_name = VALUES(last_name), role_id = VALUES(role_id), contact_no = VALUES(contact_no), status = VALUES(status);

-- Two students are marked REPEAT/batch-missed for semester 1 (repeating a failed course)
-- handled via the enrollments.status column below (student_id 29, 30).

-- ---------------------------------------------------------
-- Courses
-- ---------------------------------------------------------
INSERT INTO courses (course_code, course_name, credits, theory_sessions, practical_sessions, semester_number, academic_year, department) VALUES
('CS101', 'Programming Fundamentals', 3, 15, 15, 1, '2024/2025', 'ICT'),
('CS102', 'Computer Systems Architecture', 3, 15, 0, 1, '2024/2025', 'ICT'),
('CS103', 'Web Technologies', 3, 15, 15, 1, '2024/2025', 'ICT'),
('MA101', 'Mathematics for Computing I', 2, 15, 0, 1, '2024/2025', 'ICT'),
('EN101', 'Communication Skills', 2, 15, 0, 1, '2024/2025', 'ICT'),
('CS201', 'Data Structures & Algorithms', 4, 15, 15, 2, '2025/2026', 'ICT'),
('CS202', 'Object Oriented Programming', 3, 15, 15, 2, '2025/2026', 'ICT'),
('CS203', 'Database Management Systems', 3, 15, 15, 2, '2025/2026', 'ICT'),
('CS204', 'Computer Networks', 3, 15, 0, 2, '2025/2026', 'ICT'),
('MA201', 'Discrete Mathematics', 2, 15, 0, 2, '2025/2026', 'ICT')
ON DUPLICATE KEY UPDATE
    course_name = VALUES(course_name),
    credits = VALUES(credits),
    theory_sessions = VALUES(theory_sessions),
    practical_sessions = VALUES(practical_sessions),
    semester_number = VALUES(semester_number),
    academic_year = VALUES(academic_year),
    department = VALUES(department);

-- ---------------------------------------------------------
-- Enrollments (all 20 students in all 5 semester-1 courses;
-- all 20 students in all 5 semester-2 (current) courses)
-- ---------------------------------------------------------
INSERT INTO enrollments (student_id, course_id, semester_number, academic_year, status)
SELECT u.user_id, c.course_id, c.semester_number, c.academic_year,
       CASE WHEN c.semester_number = 1 THEN 'COMPLETED' ELSE 'ONGOING' END
FROM users u CROSS JOIN courses c
WHERE u.user_id BETWEEN 11 AND 30
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Two repeat students (29, 30) also repeating one semester-1 course
-- from an earlier failed attempt â€” already covered by the bulk enrollment
-- above; their marks below are deliberately set below the pass mark to
-- demonstrate the CA-ineligible / repeat scenario.

-- ---------------------------------------------------------
-- Marks (CA components + final exam), all out of 100
-- ---------------------------------------------------------
INSERT INTO marks (student_id, course_id, quiz_marks, assignment_marks, mid_sem_marks, practical_marks, final_exam_marks)
VALUES
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='CS101'), 64.4, 79.2, 46.3, 63.6, 75.7),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='CS102'), 78.4, 68.4, 74.5, NULL, 84.5),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='CS103'), 55.3, 90.6, 79.9, 69.6, 48.6),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='MA101'), 96.2, 72.8, 49.6, NULL, 45.3),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='EN101'), 91.4, 82.9, 85.4, NULL, 80.1),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='CS201'), 78.1, 97.0, 63.9, 78.7, 85.6),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='CS202'), 81.6, 92.7, 73.9, 85.3, 42.5),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='CS203'), 64.8, 71.0, 49.0, 65.0, 45.6),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='CS204'), 67.0, 84.2, 63.2, NULL, 60.4),
((SELECT user_id FROM users WHERE user_id=11), (SELECT course_id FROM courses WHERE course_code='MA201'), 64.0, 70.1, 91.8, NULL, 75.6),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='CS101'), 81.2, 66.5, 81.5, 62.0, 60.9),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='CS102'), 97.5, 84.3, 72.8, NULL, 77.7),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='CS103'), 91.2, 89.5, 56.5, 56.4, 57.3),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='MA101'), 66.5, 68.0, 92.1, NULL, 88.2),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='EN101'), 68.5, 84.9, 64.8, NULL, 90.3),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='CS201'), 74.7, 70.1, 57.3, 79.1, 54.5),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='CS202'), 80.1, 94.1, 65.0, 64.4, 94.9),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='CS203'), 76.9, 63.5, 47.4, 59.7, 74.5),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='CS204'), 89.1, 76.0, 48.2, NULL, 61.0),
((SELECT user_id FROM users WHERE user_id=12), (SELECT course_id FROM courses WHERE course_code='MA201'), 97.8, 80.1, 93.6, NULL, 87.3),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='CS101'), 55.5, 87.4, 79.1, 78.1, 54.7),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='CS102'), 82.6, 64.2, 66.7, NULL, 65.0),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='CS103'), 96.0, 93.3, 58.2, 76.5, 49.8),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='MA101'), 94.2, 93.1, 59.9, NULL, 75.1),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='EN101'), 81.2, 65.8, 83.1, NULL, 69.7),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='CS201'), 88.5, 80.2, 45.0, 68.9, 41.1),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='CS202'), 95.0, 93.4, 86.6, 68.2, 43.2),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='CS203'), 92.8, 96.0, 49.3, 75.9, 43.8),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='CS204'), 87.7, 89.1, 51.4, NULL, 66.1),
((SELECT user_id FROM users WHERE user_id=13), (SELECT course_id FROM courses WHERE course_code='MA201'), 78.6, 70.1, 88.6, NULL, 63.3),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='CS101'), 64.1, 80.5, 81.5, 63.6, 57.1),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='CS102'), 97.8, 84.7, 66.9, NULL, 68.5),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='CS103'), 60.2, 68.5, 61.9, 80.3, 52.7),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='MA101'), 64.5, 62.7, 76.6, NULL, 52.6),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='EN101'), 93.9, 92.7, 48.5, NULL, 53.1),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='CS201'), 83.8, 68.1, 51.6, 95.2, 71.4),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='CS202'), 75.3, 89.8, 85.4, 63.2, 45.3),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='CS203'), 73.5, 76.1, 68.4, 86.4, 77.0),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='CS204'), 97.3, 63.7, 65.1, NULL, 58.7),
((SELECT user_id FROM users WHERE user_id=14), (SELECT course_id FROM courses WHERE course_code='MA201'), 92.1, 69.4, 54.5, NULL, 64.7),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='CS101'), 73.1, 70.6, 57.5, 94.7, 64.4),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='CS102'), 92.0, 80.9, 47.5, NULL, 95.0),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='CS103'), 90.9, 96.8, 91.3, 91.5, 49.1),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='MA101'), 75.9, 68.1, 65.1, NULL, 43.2),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='EN101'), 71.3, 97.4, 58.3, NULL, 83.1),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='CS201'), 74.6, 76.1, 92.9, 97.8, 70.6),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='CS202'), 85.9, 65.9, 59.8, 96.7, 71.9),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='CS203'), 78.3, 88.4, 47.9, 80.1, 67.7),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='CS204'), 91.7, 66.0, 93.0, NULL, 44.4),
((SELECT user_id FROM users WHERE user_id=15), (SELECT course_id FROM courses WHERE course_code='MA201'), 63.0, 82.6, 78.8, NULL, 52.9),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='CS101'), 60.2, 93.8, 57.3, 80.6, 74.1),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='CS102'), 73.0, 82.2, 71.1, NULL, 91.4),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='CS103'), 63.8, 87.2, 56.9, 72.0, 76.9),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='MA101'), 67.9, 72.0, 82.6, NULL, 44.0),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='EN101'), 74.7, 97.9, 94.8, NULL, 44.0),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='CS201'), 64.2, 70.1, 91.7, 92.9, 88.4),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='CS202'), 70.9, 66.0, 86.7, 85.3, 73.6),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='CS203'), 97.5, 84.9, 45.4, 90.1, 56.5),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='CS204'), 83.5, 95.7, 51.7, NULL, 46.3),
((SELECT user_id FROM users WHERE user_id=16), (SELECT course_id FROM courses WHERE course_code='MA201'), 59.6, 81.0, 58.6, NULL, 73.3),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='CS101'), 85.9, 67.7, 76.7, 66.4, 66.9),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='CS102'), 93.9, 92.2, 49.6, NULL, 63.3),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='CS103'), 66.9, 60.1, 83.6, 82.4, 54.4),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='MA101'), 86.9, 81.0, 66.4, NULL, 40.5),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='EN101'), 58.2, 93.6, 90.2, NULL, 70.0),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='CS201'), 90.9, 82.1, 52.4, 60.5, 57.0),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='CS202'), 93.7, 90.3, 88.0, 93.7, 51.6),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='CS203'), 65.7, 63.9, 84.0, 93.0, 62.4),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='CS204'), 24.3, 22.3, 28.9, NULL, 51.6),
((SELECT user_id FROM users WHERE user_id=17), (SELECT course_id FROM courses WHERE course_code='MA201'), 97.0, 90.8, 89.1, NULL, 41.4),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='CS101'), 86.7, 72.6, 91.5, 89.5, 87.5),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='CS102'), 89.9, 70.1, 84.4, NULL, 45.9),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='CS103'), 92.5, 92.6, 56.1, 90.1, 65.3),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='MA101'), 68.1, 90.2, 56.4, NULL, 41.3),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='EN101'), 63.3, 72.5, 88.2, NULL, 93.2),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='CS201'), 67.0, 84.4, 65.0, 97.2, 69.5),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='CS202'), 95.4, 64.4, 93.5, 62.7, 92.9),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='CS203'), 66.4, 64.1, 66.7, 86.3, 57.3),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='CS204'), 81.1, 79.4, 64.3, NULL, 71.7),
((SELECT user_id FROM users WHERE user_id=18), (SELECT course_id FROM courses WHERE course_code='MA201'), 66.0, 86.9, 45.1, NULL, 90.9),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='CS101'), 78.2, 87.3, 82.1, 83.8, 60.0),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='CS102'), 58.0, 85.2, 61.5, NULL, 57.3),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='CS103'), 91.5, 87.4, 60.0, 68.3, 62.5),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='MA101'), 72.3, 71.2, 51.4, NULL, 63.1),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='EN101'), 95.4, 85.7, 90.1, NULL, 73.9),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='CS201'), 67.9, 80.8, 45.0, 67.3, 63.6),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='CS202'), 79.9, 84.9, 68.2, 74.0, 51.8),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='CS203'), 75.3, 94.2, 84.8, 62.3, 44.7),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='CS204'), 77.2, 84.1, 61.8, NULL, 85.0),
((SELECT user_id FROM users WHERE user_id=19), (SELECT course_id FROM courses WHERE course_code='MA201'), 87.3, 85.6, 56.2, NULL, 51.0),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='CS101'), 56.1, 69.3, 68.8, 91.5, 44.0),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='CS102'), 72.8, 83.9, 54.7, NULL, 78.3),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='CS103'), 76.3, 69.3, 77.8, 55.2, 81.3),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='MA101'), 88.1, 64.1, 66.3, NULL, 49.7),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='EN101'), 96.2, 79.7, 47.5, NULL, 53.7),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='CS201'), 91.5, 77.3, 85.1, 83.7, 94.3),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='CS202'), 80.6, 96.1, 89.6, 81.3, 79.6),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='CS203'), 76.7, 91.6, 72.4, 93.6, 80.9),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='CS204'), 75.4, 69.8, 57.4, NULL, 75.1),
((SELECT user_id FROM users WHERE user_id=20), (SELECT course_id FROM courses WHERE course_code='MA201'), 87.9, 79.8, 76.3, NULL, 55.1),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='CS101'), 58.3, 70.9, 58.6, 68.7, 69.7),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='CS102'), 61.0, 68.8, 79.7, NULL, 78.9),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='CS103'), 57.8, 75.5, 72.1, 72.9, 51.4),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='MA101'), 73.1, 94.4, 74.2, NULL, 78.3),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='EN101'), 91.8, 89.1, 64.0, NULL, 40.3),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='CS201'), 70.1, 88.6, 87.7, 96.0, 63.0),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='CS202'), 87.1, 80.8, 75.2, 64.5, 52.1),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='CS203'), 73.7, 61.1, 61.8, 84.2, 62.2),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='CS204'), 62.1, 77.8, 51.4, NULL, 74.2),
((SELECT user_id FROM users WHERE user_id=21), (SELECT course_id FROM courses WHERE course_code='MA201'), 56.2, 75.0, 73.2, NULL, 41.5),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='CS101'), 82.6, 65.2, 68.1, 57.2, 60.9),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='CS102'), 64.1, 72.4, 83.1, NULL, 60.9),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='CS103'), 87.3, 91.6, 57.6, 58.5, 41.1),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='MA101'), 78.2, 98.0, 62.5, NULL, 75.8),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='EN101'), 88.6, 84.8, 82.7, NULL, 92.2),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='CS201'), 63.6, 60.8, 52.6, 60.4, 76.8),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='CS202'), 79.3, 68.3, 80.0, 88.0, 49.2),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='CS203'), 81.1, 88.4, 50.7, 90.2, 93.1),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='CS204'), 59.6, 61.0, 60.6, NULL, 77.3),
((SELECT user_id FROM users WHERE user_id=22), (SELECT course_id FROM courses WHERE course_code='MA201'), 29.4, 25.9, 25.7, NULL, 31.9),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='CS101'), 84.7, 83.8, 50.1, 88.2, 86.8),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='CS102'), 80.8, 64.6, 94.2, NULL, 83.0),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='CS103'), 69.9, 76.3, 63.5, 76.8, 58.8),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='MA101'), 91.5, 91.2, 50.3, NULL, 92.8),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='EN101'), 82.3, 91.5, 80.4, NULL, 64.0),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='CS201'), 86.6, 96.7, 58.5, 89.8, 69.6),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='CS202'), 75.8, 76.6, 81.6, 66.5, 86.8),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='CS203'), 90.7, 63.3, 89.1, 65.5, 65.6),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='CS204'), 81.2, 74.4, 46.4, NULL, 86.8),
((SELECT user_id FROM users WHERE user_id=23), (SELECT course_id FROM courses WHERE course_code='MA201'), 62.8, 68.1, 84.9, NULL, 58.7),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='CS101'), 92.9, 86.6, 58.8, 55.4, 92.1),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='CS102'), 58.7, 87.4, 69.4, NULL, 81.7),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='CS103'), 84.7, 84.5, 69.5, 89.1, 45.1),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='MA101'), 64.5, 86.3, 60.3, NULL, 72.0),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='EN101'), 75.4, 80.2, 66.3, NULL, 81.0),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='CS201'), 69.2, 86.7, 58.5, 65.8, 46.6),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='CS202'), 63.3, 64.5, 71.8, 87.8, 50.2),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='CS203'), 64.3, 78.4, 81.2, 97.0, 68.9),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='CS204'), 67.2, 63.8, 54.7, NULL, 52.5),
((SELECT user_id FROM users WHERE user_id=24), (SELECT course_id FROM courses WHERE course_code='MA201'), 62.7, 60.5, 71.7, NULL, 55.1),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='CS101'), 96.9, 81.0, 79.9, 60.4, 87.8),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='CS102'), 76.1, 93.2, 73.7, NULL, 65.8),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='CS103'), 73.9, 67.0, 47.6, 95.5, 66.3),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='MA101'), 90.4, 75.2, 48.7, NULL, 74.6),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='EN101'), 57.3, 65.7, 73.1, NULL, 56.7),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='CS201'), 97.7, 64.5, 83.2, 81.1, 83.5),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='CS202'), 64.7, 79.9, 67.5, 74.0, 87.3),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='CS203'), 97.6, 71.6, 76.1, 81.2, 80.7),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='CS204'), 95.7, 67.9, 55.6, NULL, 76.3),
((SELECT user_id FROM users WHERE user_id=25), (SELECT course_id FROM courses WHERE course_code='MA201'), 61.8, 66.6, 48.8, NULL, 40.1),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='CS101'), 74.4, 82.6, 59.6, 65.0, 78.9),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='CS102'), 85.2, 77.3, 79.4, NULL, 90.8),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='CS103'), 88.9, 83.8, 78.1, 95.1, 63.4),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='MA101'), 78.4, 84.6, 90.4, NULL, 85.5),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='EN101'), 58.1, 66.3, 60.4, NULL, 81.2),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='CS201'), 79.5, 71.0, 51.2, 84.6, 78.5),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='CS202'), 95.5, 79.0, 69.7, 58.5, 42.2),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='CS203'), 73.6, 72.2, 57.5, 58.9, 92.9),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='CS204'), 90.9, 81.9, 92.5, NULL, 95.0),
((SELECT user_id FROM users WHERE user_id=26), (SELECT course_id FROM courses WHERE course_code='MA201'), 83.9, 70.2, 47.0, NULL, 81.6),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='CS101'), 75.2, 84.8, 90.8, 62.8, 72.2),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='CS102'), 82.3, 78.7, 49.6, NULL, 59.1),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='CS103'), 69.3, 85.5, 87.9, 69.2, 78.2),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='MA101'), 67.4, 95.9, 85.7, NULL, 70.3),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='EN101'), 74.6, 72.0, 61.2, NULL, 93.4),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='CS201'), 72.4, 79.6, 94.4, 83.3, 69.8),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='CS202'), 72.8, 67.1, 63.1, 87.5, 74.4),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='CS203'), 87.7, 67.7, 72.5, 94.9, 64.1),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='CS204'), 85.0, 64.6, 93.7, NULL, 73.5),
((SELECT user_id FROM users WHERE user_id=27), (SELECT course_id FROM courses WHERE course_code='MA201'), 65.3, 66.0, 72.5, NULL, 70.4),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='CS101'), 59.0, 97.7, 90.6, 74.8, 46.5),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='CS102'), 90.8, 78.9, 80.8, NULL, 68.0),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='CS103'), 66.8, 91.7, 94.0, 65.5, 70.3),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='MA101'), 71.5, 95.0, 70.4, NULL, 88.4),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='EN101'), 92.2, 70.5, 84.5, NULL, 62.8),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='CS201'), 95.2, 79.3, 86.0, 67.2, 56.4),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='CS202'), 80.2, 98.0, 69.5, 61.4, 69.6),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='CS203'), 69.8, 81.0, 72.2, 74.6, 57.7),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='CS204'), 63.1, 86.5, 73.6, NULL, 52.8),
((SELECT user_id FROM users WHERE user_id=28), (SELECT course_id FROM courses WHERE course_code='MA201'), 88.3, 61.7, 82.2, NULL, 78.8),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='CS101'), 27.2, 25.8, 25.0, 37.3, 54.5),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='CS102'), 76.3, 61.4, 70.1, NULL, 72.5),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='CS103'), 92.4, 93.2, 67.0, 77.6, 65.1),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='MA101'), 86.1, 75.6, 77.7, NULL, 48.5),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='EN101'), 75.2, 96.8, 61.9, NULL, 78.1),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='CS201'), 82.9, 92.4, 87.6, 92.0, 60.9),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='CS202'), 68.6, 87.3, 83.0, 92.5, 42.0),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='CS203'), 57.9, 84.0, 91.0, 97.9, 81.1),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='CS204'), 73.7, 63.7, 76.7, NULL, 88.0),
((SELECT user_id FROM users WHERE user_id=29), (SELECT course_id FROM courses WHERE course_code='MA201'), 74.1, 86.4, 90.2, NULL, 42.5),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='CS101'), 26.9, 24.4, 20.6, 27.2, 43.3),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='CS102'), 79.3, 90.1, 53.5, NULL, 44.3),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='CS103'), 92.4, 83.5, 57.0, 94.3, 47.9),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='MA101'), 74.8, 69.7, 57.8, NULL, 40.5),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='EN101'), 89.6, 94.2, 78.9, NULL, 48.7),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='CS201'), 74.0, 73.1, 74.4, 82.5, 63.3),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='CS202'), 65.8, 92.1, 55.0, 71.5, 66.6),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='CS203'), 65.2, 81.7, 73.7, 97.7, 56.2),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='CS204'), 97.1, 85.0, 58.7, NULL, 71.1),
((SELECT user_id FROM users WHERE user_id=30), (SELECT course_id FROM courses WHERE course_code='MA201'), 84.5, 88.3, 47.5, NULL, 73.4)
ON DUPLICATE KEY UPDATE quiz_marks=VALUES(quiz_marks), assignment_marks=VALUES(assignment_marks), mid_sem_marks=VALUES(mid_sem_marks), practical_marks=VALUES(practical_marks), final_exam_marks=VALUES(final_exam_marks);

-- ---------------------------------------------------------
-- Timetable (current semester â€” Semester 2, AY 2025/2026)
-- ---------------------------------------------------------
INSERT INTO timetable (course_id, day_of_week, start_time, end_time, session_type, lecturer_name, venue, semester_number, academic_year) VALUES
((SELECT course_id FROM courses WHERE course_code='CS201'), 'MONDAY', '08:00:00', '10:00:00', 'THEORY', 'Mr. Kamal Perera', 'Lecture Hall A1', 2, '2025/2026'),
((SELECT course_id FROM courses WHERE course_code='CS201'), 'WEDNESDAY', '13:00:00', '15:00:00', 'PRACTICAL', 'Mr. Kamal Perera', 'Computer Lab 1', 2, '2025/2026'),
((SELECT course_id FROM courses WHERE course_code='CS202'), 'MONDAY', '10:00:00', '12:00:00', 'THEORY', 'Ms. Amara Jayasinghe', 'Lecture Hall A2', 2, '2025/2026'),
((SELECT course_id FROM courses WHERE course_code='CS202'), 'THURSDAY', '09:00:00', '11:00:00', 'PRACTICAL', 'Ms. Amara Jayasinghe', 'Computer Lab 2', 2, '2025/2026'),
((SELECT course_id FROM courses WHERE course_code='CS203'), 'TUESDAY', '08:00:00', '10:00:00', 'THEORY', 'Mr. Nimal Silva', 'Lecture Hall B1', 2, '2025/2026'),
((SELECT course_id FROM courses WHERE course_code='CS203'), 'FRIDAY', '13:00:00', '15:00:00', 'PRACTICAL', 'Mr. Nimal Silva', 'Computer Lab 1', 2, '2025/2026'),
((SELECT course_id FROM courses WHERE course_code='CS204'), 'TUESDAY', '10:00:00', '12:00:00', 'THEORY', 'Mr. Sunil Fernando', 'Lecture Hall B2', 2, '2025/2026'),
((SELECT course_id FROM courses WHERE course_code='MA201'), 'WEDNESDAY', '08:00:00', '10:00:00', 'THEORY', 'Ms. Champa Wickramasinghe', 'Lecture Hall A1', 2, '2025/2026');

-- ---------------------------------------------------------
-- Attendance summaries for the student portal demo.
-- Students can view these records but cannot edit them.
-- ---------------------------------------------------------
INSERT INTO attendance_records (student_id, course_id, theory_attended, theory_total, practical_attended, practical_total)
SELECT e.student_id, e.course_id,
       GREATEST(0, c.theory_sessions - 2), c.theory_sessions,
       GREATEST(0, c.practical_sessions - 1), c.practical_sessions
FROM enrollments e JOIN courses c ON c.course_id = e.course_id
ON DUPLICATE KEY UPDATE theory_attended = VALUES(theory_attended), theory_total = VALUES(theory_total),
                        practical_attended = VALUES(practical_attended), practical_total = VALUES(practical_total);


-- ------------------------------------------------------------------------------
-- File: database/06_insert_attendance_medical.sql
-- ------------------------------------------------------------------------------
-- ================================================================================
-- Seed Script: Member 3 â€” Attendance Sessions, Attendance Records & Medicals
-- Faculty Management System (Faculty of Technology, University of Ruhuna)
-- Course Unit: ICT2132 - Object Oriented Programming Practicum
--
-- This script pre-populates realistic semester session data across 15 sessions 
-- (Theory and Practical) covering all 5 mandatory evaluation scenarios:
--   1. Undergraduates with > 80% attendance
--   2. Undergraduates with exactly 80% attendance (12/15 sessions = 80.0%)
--   3. Undergraduates with < 80% attendance without medical certificates
--   4. Undergraduates with > 80% attendance with approved medicals
--   5. Undergraduates with < 80% attendance but with approved medicals reaching >= 80%
-- ================================================================================

-- database/06_insert_attendance_medical.sql Seed Data

-- --------------------------------------------------------------------------------
-- 0. Ensure tables exist before inserting records
-- --------------------------------------------------------------------------------
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
    UNIQUE KEY unique_session_student (session_id, student_id)
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

-- --------------------------------------------------------------------------------
-- 1. Ensure `session_number` column exists in `attendance_sessions`
-- --------------------------------------------------------------------------------
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'attendance_sessions' 
                   AND COLUMN_NAME = 'session_number');
SET @alter_sql = IF(@col_exists = 0, 'ALTER TABLE attendance_sessions ADD COLUMN session_number INT DEFAULT 1', 'SELECT 1');
PREPARE stmt_alter FROM @alter_sql;
EXECUTE stmt_alter;
DEALLOCATE PREPARE stmt_alter;

-- --------------------------------------------------------------------------------
-- 2. Populate 15 Theory & 15 Practical Sessions for Course Modules
--    Course: ICT2112 (Data Structures and Algorithms)
--    Course: ICT2123 (Database Management Systems)
--    Course: CS201   (Data Structures & Algorithms - Alternative Code)
-- --------------------------------------------------------------------------------
INSERT INTO attendance_sessions (session_id, course_id, session_type, session_number, session_date, hours, created_by) VALUES
-- ICT2112 Theory Sessions (1 to 15)
(1,  'ICT2112', 'THEORY', 1,  '2026-06-08', 2.0, 7),
(2,  'ICT2112', 'THEORY', 2,  '2026-06-15', 2.0, 7),
(3,  'ICT2112', 'THEORY', 3,  '2026-06-22', 2.0, 7),
(4,  'ICT2112', 'THEORY', 4,  '2026-06-29', 2.0, 7),
(5,  'ICT2112', 'THEORY', 5,  '2026-07-06', 2.0, 7),
(6,  'ICT2112', 'THEORY', 6,  '2026-07-13', 2.0, 7),
(7,  'ICT2112', 'THEORY', 7,  '2026-07-20', 2.0, 7),
(8,  'ICT2112', 'THEORY', 8,  '2026-07-27', 2.0, 7),
(9,  'ICT2112', 'THEORY', 9,  '2026-08-03', 2.0, 7),
(10, 'ICT2112', 'THEORY', 10, '2026-08-10', 2.0, 7),
(11, 'ICT2112', 'THEORY', 11, '2026-08-17', 2.0, 7),
(12, 'ICT2112', 'THEORY', 12, '2026-08-24', 2.0, 7),
(13, 'ICT2112', 'THEORY', 13, '2026-08-31', 2.0, 7),
(14, 'ICT2112', 'THEORY', 14, '2026-09-07', 2.0, 7),
(15, 'ICT2112', 'THEORY', 15, '2026-09-14', 2.0, 7),

-- ICT2112 Practical / Lab Sessions (1 to 15)
(16, 'ICT2112', 'PRACTICAL', 1,  '2026-06-10', 2.0, 7),
(17, 'ICT2112', 'PRACTICAL', 2,  '2026-06-17', 2.0, 7),
(18, 'ICT2112', 'PRACTICAL', 3,  '2026-06-24', 2.0, 7),
(19, 'ICT2112', 'PRACTICAL', 4,  '2026-07-01', 2.0, 7),
(20, 'ICT2112', 'PRACTICAL', 5,  '2026-07-08', 2.0, 7),
(21, 'ICT2112', 'PRACTICAL', 6,  '2026-07-15', 2.0, 7),
(22, 'ICT2112', 'PRACTICAL', 7,  '2026-07-22', 2.0, 7),
(23, 'ICT2112', 'PRACTICAL', 8,  '2026-07-29', 2.0, 7),
(24, 'ICT2112', 'PRACTICAL', 9,  '2026-08-05', 2.0, 7),
(25, 'ICT2112', 'PRACTICAL', 10, '2026-08-12', 2.0, 7),
(26, 'ICT2112', 'PRACTICAL', 11, '2026-08-19', 2.0, 7),
(27, 'ICT2112', 'PRACTICAL', 12, '2026-08-26', 2.0, 7),
(28, 'ICT2112', 'PRACTICAL', 13, '2026-09-02', 2.0, 7),
(29, 'ICT2112', 'PRACTICAL', 14, '2026-09-09', 2.0, 7),
(30, 'ICT2112', 'PRACTICAL', 15, '2026-09-16', 2.0, 7),

-- ICT2123 Theory Sessions (1 to 15)
(31, 'ICT2123', 'THEORY', 1,  '2026-06-09', 2.0, 7),
(32, 'ICT2123', 'THEORY', 2,  '2026-06-16', 2.0, 7),
(33, 'ICT2123', 'THEORY', 3,  '2026-06-23', 2.0, 7),
(34, 'ICT2123', 'THEORY', 4,  '2026-06-30', 2.0, 7),
(35, 'ICT2123', 'THEORY', 5,  '2026-07-07', 2.0, 7),
(36, 'ICT2123', 'THEORY', 6,  '2026-07-14', 2.0, 7),
(37, 'ICT2123', 'THEORY', 7,  '2026-07-21', 2.0, 7),
(38, 'ICT2123', 'THEORY', 8,  '2026-07-28', 2.0, 7),
(39, 'ICT2123', 'THEORY', 9,  '2026-08-04', 2.0, 7),
(40, 'ICT2123', 'THEORY', 10, '2026-08-11', 2.0, 7),
(41, 'ICT2123', 'THEORY', 11, '2026-08-18', 2.0, 7),
(42, 'ICT2123', 'THEORY', 12, '2026-08-25', 2.0, 7),
(43, 'ICT2123', 'THEORY', 13, '2026-09-01', 2.0, 7),
(44, 'ICT2123', 'THEORY', 14, '2026-09-08', 2.0, 7),
(45, 'ICT2123', 'THEORY', 15, '2026-09-15', 2.0, 7),

-- ICT2123 Practical / Lab Sessions (1 to 15)
(46, 'ICT2123', 'PRACTICAL', 1,  '2026-06-11', 2.0, 7),
(47, 'ICT2123', 'PRACTICAL', 2,  '2026-06-18', 2.0, 7),
(48, 'ICT2123', 'PRACTICAL', 3,  '2026-06-25', 2.0, 7),
(49, 'ICT2123', 'PRACTICAL', 4,  '2026-07-02', 2.0, 7),
(50, 'ICT2123', 'PRACTICAL', 5,  '2026-07-09', 2.0, 7),
(51, 'ICT2123', 'PRACTICAL', 6,  '2026-07-16', 2.0, 7),
(52, 'ICT2123', 'PRACTICAL', 7,  '2026-07-23', 2.0, 7),
(53, 'ICT2123', 'PRACTICAL', 8,  '2026-07-30', 2.0, 7),
(54, 'ICT2123', 'PRACTICAL', 9,  '2026-08-06', 2.0, 7),
(55, 'ICT2123', 'PRACTICAL', 10, '2026-08-13', 2.0, 7),
(56, 'ICT2123', 'PRACTICAL', 11, '2026-08-20', 2.0, 7),
(57, 'ICT2123', 'PRACTICAL', 12, '2026-08-27', 2.0, 7),
(58, 'ICT2123', 'PRACTICAL', 13, '2026-09-03', 2.0, 7),
(59, 'ICT2123', 'PRACTICAL', 14, '2026-09-10', 2.0, 7),
(60, 'ICT2123', 'PRACTICAL', 15, '2026-09-17', 2.0, 7),

-- CS201 Theory Sessions (1 to 15) for compatibility with Member 4
(61, 'CS201', 'THEORY', 1,  '2026-06-08', 2.0, 7),
(62, 'CS201', 'THEORY', 2,  '2026-06-15', 2.0, 7),
(63, 'CS201', 'THEORY', 3,  '2026-06-22', 2.0, 7),
(64, 'CS201', 'THEORY', 4,  '2026-06-29', 2.0, 7),
(65, 'CS201', 'THEORY', 5,  '2026-07-06', 2.0, 7),
(66, 'CS201', 'THEORY', 6,  '2026-07-13', 2.0, 7),
(67, 'CS201', 'THEORY', 7,  '2026-07-20', 2.0, 7),
(68, 'CS201', 'THEORY', 8,  '2026-07-27', 2.0, 7),
(69, 'CS201', 'THEORY', 9,  '2026-08-03', 2.0, 7),
(70, 'CS201', 'THEORY', 10, '2026-08-10', 2.0, 7),
(71, 'CS201', 'THEORY', 11, '2026-08-17', 2.0, 7),
(72, 'CS201', 'THEORY', 12, '2026-08-24', 2.0, 7),
(73, 'CS201', 'THEORY', 13, '2026-08-31', 2.0, 7),
(74, 'CS201', 'THEORY', 14, '2026-09-07', 2.0, 7),
(75, 'CS201', 'THEORY', 15, '2026-09-14', 2.0, 7),

-- CS201 Practical / Lab Sessions (1 to 15)
(76, 'CS201', 'PRACTICAL', 1,  '2026-06-10', 2.0, 7),
(77, 'CS201', 'PRACTICAL', 2,  '2026-06-17', 2.0, 7),
(78, 'CS201', 'PRACTICAL', 3,  '2026-06-24', 2.0, 7),
(79, 'CS201', 'PRACTICAL', 4,  '2026-07-01', 2.0, 7),
(80, 'CS201', 'PRACTICAL', 5,  '2026-07-08', 2.0, 7),
(81, 'CS201', 'PRACTICAL', 6,  '2026-07-15', 2.0, 7),
(82, 'CS201', 'PRACTICAL', 7,  '2026-07-22', 2.0, 7),
(83, 'CS201', 'PRACTICAL', 8,  '2026-07-29', 2.0, 7),
(84, 'CS201', 'PRACTICAL', 9,  '2026-08-05', 2.0, 7),
(85, 'CS201', 'PRACTICAL', 10, '2026-08-12', 2.0, 7),
(86, 'CS201', 'PRACTICAL', 11, '2026-08-19', 2.0, 7),
(87, 'CS201', 'PRACTICAL', 12, '2026-08-26', 2.0, 7),
(88, 'CS201', 'PRACTICAL', 13, '2026-09-02', 2.0, 7),
(89, 'CS201', 'PRACTICAL', 14, '2026-09-09', 2.0, 7),
(90, 'CS201', 'PRACTICAL', 15, '2026-09-16', 2.0, 7)
ON DUPLICATE KEY UPDATE 
    session_date = VALUES(session_date), 
    hours = VALUES(hours),
    created_by = VALUES(created_by);

-- --------------------------------------------------------------------------------
-- 3. Populate Medical Records (Covering Approved, Pending, Rejected Scenarios)
-- --------------------------------------------------------------------------------
INSERT INTO medical_records (medical_id, student_id, course_id, session_id, medical_date, reason, status) VALUES
-- Scenario 4: Students with > 80% attendance who also submitted medicals
(1, 22, 'ICT2112', 14, '2026-09-07', 'Acute tonsillitis - Treated at District General Hospital Galle (Medical cert attached)', 'APPROVED'),
(2, 23, 'ICT2112', 15, '2026-09-14', 'Severe tooth extraction recovery and pain management under Dental Surgeon', 'APPROVED'),
(3, 24, 'ICT2112', 15, '2026-09-14', 'Sprained ankle during inter-faculty badminton tournament - Dr. Perera (GMO)', 'APPROVED'),

-- Scenario 5: Students with < 80% attendance whose approved medicals lift them to >= 80%
-- Student 25 (tg2021015): 10 attended + 3 approved medicals = 13/15 = 86.7% (ELIGIBLE)
(4, 25, 'ICT2112', 11, '2026-08-17', 'Viral influenza with high fever admitted to Karapitiya Teaching Hospital', 'APPROVED'),
(5, 25, 'ICT2112', 12, '2026-08-24', 'Post-viral asthenia and convalescence prescribed by Consultant Physician', 'APPROVED'),
(6, 25, 'ICT2112', 13, '2026-08-31', 'Follow-up clinical examination and blood investigations at Teaching Hospital', 'APPROVED'),

-- Student 26 (tg2021016): 10 attended + 3 approved medicals = 13/15 = 86.7% (ELIGIBLE)
(7, 26, 'ICT2112', 11, '2026-08-17', 'Acute food poisoning and gastroenteritis with dehydration', 'APPROVED'),
(8, 26, 'ICT2112', 12, '2026-08-24', 'Medical leave certified by University Medical Center Medical Officer', 'APPROVED'),
(9, 26, 'ICT2112', 13, '2026-08-31', 'Physical therapy sessions for severe lumbar muscle strain', 'APPROVED'),

-- Student 27 (tg2021017): 11 attended + 2 approved medicals = 13/15 = 86.7% (ELIGIBLE)
(10, 27, 'ICT2112', 12, '2026-08-25', 'Dengue fever recovery and complete bed rest recommended by GMO', 'APPROVED'),
(11, 27, 'ICT2112', 13, '2026-09-01', 'Platelet count monitoring and clinical follow-up', 'APPROVED'),

-- Students with Pending Review Medicals (Awaiting Technical Officer Action)
(12, 28, 'ICT2112', 13, '2026-08-31', 'Severe allergic reaction and eye infection - Medical certificate submitted', 'PENDING'),
(13, 28, 'ICT2112', 14, '2026-09-07', 'Dermatological specialist consultation at General Hospital', 'PENDING'),

-- Repeat / Batch Missed Students with Rejected Medicals (Uncertified clinic note)
(14, 29, 'ICT2112', 10, '2026-08-10', 'Private clinic prescription without authorized doctor registration number', 'REJECTED'),
(15, 30, 'ICT2112', 10, '2026-08-10', 'Submitted after prescribed 7-day regulation window without faculty endorsement', 'REJECTED')
ON DUPLICATE KEY UPDATE
    reason = VALUES(reason),
    status = VALUES(status);

-- --------------------------------------------------------------------------------
-- 4. Populate Attendance Records for all 20 Students (IDs 11 to 30)
--    Satisfying All 5 Required Test Scenarios for ICT2112 Theory (Sessions 1 to 15)
-- --------------------------------------------------------------------------------

-- Scenario 1: > 80% Attendance (Kasun, Nuwan, Dinuka, Sajith - IDs 11, 12, 13, 14)
-- Kasun & Nuwan: 15/15 = 100%
-- Dinuka & Sajith: 14/15 = 93.3%
INSERT INTO attendance_records (session_id, student_id, status)
SELECT s.session_id, u.user_id, 'PRESENT'
FROM attendance_sessions s
CROSS JOIN users u
WHERE s.session_id BETWEEN 1 AND 15
  AND u.user_id IN (11, 12)
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO attendance_records (session_id, student_id, status)
SELECT s.session_id, u.user_id, 
       IF(s.session_number = 15, 'ABSENT', 'PRESENT')
FROM attendance_sessions s
CROSS JOIN users u
WHERE s.session_id BETWEEN 1 AND 15
  AND u.user_id IN (13, 14)
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Scenario 2: Exactly 80% Attendance (Ishara, Tharindu, Chamodi - IDs 15, 16, 17)
-- Exactly 12/15 = 80.0% (Sessions 1-12 Present; Sessions 13-15 Absent)
INSERT INTO attendance_records (session_id, student_id, status)
SELECT s.session_id, u.user_id, 
       IF(s.session_number <= 12, 'PRESENT', 'ABSENT')
FROM attendance_sessions s
CROSS JOIN users u
WHERE s.session_id BETWEEN 1 AND 15
  AND u.user_id IN (15, 16, 17)
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Scenario 3: < 80% Attendance WITHOUT Medicals (Yasas, Hiruni, Dulaj, Sanduni - IDs 18, 19, 20, 21)
-- 9/15 = 60.0% (Sessions 1-9 Present; Sessions 10-15 Absent; No medicals) -> INELIGIBLE / SHORTAGE
INSERT INTO attendance_records (session_id, student_id, status)
SELECT s.session_id, u.user_id, 
       IF(s.session_number <= 9, 'PRESENT', 'ABSENT')
FROM attendance_sessions s
CROSS JOIN users u
WHERE s.session_id BETWEEN 1 AND 15
  AND u.user_id IN (18, 19, 20, 21)
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Scenario 4: > 80% Attendance WITH Medicals (Ravindu, Vihanga, Nethmi - IDs 22, 23, 24)
-- 13/15 Attended = 86.7% (>80%) + Medicals on record for absent sessions 14, 15
INSERT INTO attendance_records (session_id, student_id, status)
SELECT s.session_id, u.user_id, 
       IF(s.session_number <= 13, 'PRESENT', 'ABSENT')
FROM attendance_sessions s
CROSS JOIN users u
WHERE s.session_id BETWEEN 1 AND 15
  AND u.user_id IN (22, 23, 24)
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Scenario 5: < 80% Attendance WITH Approved Medicals reaching >= 80% (Oshadi, Malith, Piumi - IDs 25, 26, 27)
-- 10/15 Attended = 66.7% (<80%), but 3 Approved Medicals (sessions 11, 12, 13) -> 13/15 = 86.7% (ELIGIBLE)
INSERT INTO attendance_records (session_id, student_id, status)
SELECT s.session_id, u.user_id, 
       IF(s.session_number <= 10, 'PRESENT', 'ABSENT')
FROM attendance_sessions s
CROSS JOIN users u
WHERE s.session_id BETWEEN 1 AND 15
  AND u.user_id IN (25, 26, 27)
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- Pending & Repeat / Batch-Missed Students (Isuru, Kavindu, Sathmini - IDs 28, 29, 30)
-- IDs 29, 30: 7/15 Attended = 46.7%, medicals rejected -> Ineligible repeat student
INSERT INTO attendance_records (session_id, student_id, status)
SELECT s.session_id, u.user_id, 
       IF(s.session_number <= 7, 'PRESENT', 'ABSENT')
FROM attendance_sessions s
CROSS JOIN users u
WHERE s.session_id BETWEEN 1 AND 15
  AND u.user_id IN (28, 29, 30)
ON DUPLICATE KEY UPDATE status = VALUES(status);


-- --------------------------------------------------------------------------------
-- 5. Duplicate the pattern for Practical Sessions (16 to 30)
-- --------------------------------------------------------------------------------
INSERT INTO attendance_records (session_id, student_id, status)
SELECT (ar.session_id + 15), ar.student_id, ar.status
FROM attendance_records ar
WHERE ar.session_id BETWEEN 1 AND 15
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- --------------------------------------------------------------------------------
-- 6. Also Duplicate for ICT2123 (Sessions 31 to 60) and CS201 (Sessions 61 to 90)
--    so every course selected in UI displays populated attendance data
-- --------------------------------------------------------------------------------
INSERT INTO attendance_records (session_id, student_id, status)
SELECT (ar.session_id + 30), ar.student_id, ar.status
FROM attendance_records ar
WHERE ar.session_id BETWEEN 1 AND 30
ON DUPLICATE KEY UPDATE status = VALUES(status);

INSERT INTO attendance_records (session_id, student_id, status)
SELECT (ar.session_id + 60), ar.student_id, ar.status
FROM attendance_records ar
WHERE ar.session_id BETWEEN 1 AND 30
ON DUPLICATE KEY UPDATE status = VALUES(status);

-- ================================================================================
-- Verification Query Summary:
-- ================================================================================
-- SELECT 
--     c.course_id, 
--     s.session_type, 
--     COUNT(DISTINCT s.session_id) AS total_sessions,
--     COUNT(ar.record_id) AS total_marked_records
-- FROM attendance_sessions s
-- JOIN courses c ON s.course_id = c.course_code
-- LEFT JOIN attendance_records ar ON s.session_id = ar.session_id
-- GROUP BY c.course_id, s.session_type;


-- ------------------------------------------------------------------------------
-- File: database/10_insert_notices.sql
-- ------------------------------------------------------------------------------
-- =======================================================
-- Seed Script: Notices & Announcements
-- Faculty Management System
-- =======================================================

-- database/10_insert_notices.sql Seed Data

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

INSERT INTO notices (title, content, category, priority, posted_by, posted_date) VALUES
('Mandatory 80% Attendance Requirement for Semester 2 Examinations', 'All undergraduates of the Faculty of Technology are hereby reminded that an overall attendance of at least 80% in both theory lectures and laboratory sessions is mandatory to be deemed eligible to sit for the upcoming semester final examinations. Students falling below 80% must immediately verify that all medical certificates have been formally lodged with the Technical Officer.', 'ACADEMIC', 'URGENT', 'Department of Information and Communication Technology', CURDATE()),
('Continuous Assessment (CA >= 40%) Eligibility Lists Published', 'The provisional Continuous Assessment (CA) scores and eligibility statuses for Level II Semester I modules (CS201, CS202, CS203, CS204, MA201) have been compiled. In accordance with university curriculum criteria, students must achieve a minimum CA score of 40% out of 100 to sit for the respective end-semester exam. Please check your individual grade transcript.', 'EXAMINATION', 'URGENT', 'Examination Division & Course Coordinators', CURDATE()),
('Laboratory Session Venue Adjustment â€” Computer Lab 1 & 2', 'Commencing Monday next week, practical laboratory sessions for Object Oriented Programming (CS202) and Database Management Systems (CS203) will be conducted in Computer Lab 2 (Level 2, Technology Complex). Please refer to the updated class timetable on your student dashboard.', 'TIMETABLE', 'NORMAL', 'Technical Officer in Charge', CURDATE()),
('Submission Deadline for Outstanding Medical Certificates', 'Undergraduates with excused absences between Week 1 and Week 10 must lodge certified medical certificates within 7 days of this announcement. Certificates must be issued by a registered Government Medical Officer (GMO) and uploaded as a PDF via the student portal Medical tab.', 'GENERAL', 'NORMAL', 'Senior Assistant Registrar / Academic', CURDATE()),
('UGC Circular No. 12-2024 Grading System Implementation Guidelines', 'Official notification regarding the standard Sri Lankan University Grants Commission (UGC) Circular No. 12-2024 grading criteria. Letter grades (A+ to E) and corresponding Grade Point Values (4.00 to 0.00) are mapped based on 40% CA + 60% Final Examination weighting. Cumulative CGPA classifications are available on the CGPA breakdown tab.', 'ACADEMIC', 'INFO', 'Dean\'s Office, Faculty of Technology', CURDATE());


-- ------------------------------------------------------------------------------
-- Finalize Script
-- ------------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 1;

-- Verification query
SELECT 'Database setup completed successfully!' AS status,
       (SELECT COUNT(*) FROM roles) AS roles_count,
       (SELECT COUNT(*) FROM users) AS users_count,
       (SELECT COUNT(*) FROM courses) AS courses_count,
       (SELECT COUNT(*) FROM marks) AS marks_count,
       (SELECT COUNT(*) FROM notices) AS notices_count;
