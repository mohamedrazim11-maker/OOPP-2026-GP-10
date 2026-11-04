-- ==============================================================================
-- Microsoft SQL Server Setup & Seed Script
-- Faculty Management System (FMS) | University of Ruhuna - Faculty of Technology
-- ==============================================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'faculty_management_db')
BEGIN
    CREATE DATABASE [faculty_management_db];
END
GO

USE [faculty_management_db];
GO

-- 1. Roles Table
IF OBJECT_ID(N'dbo.roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.roles (
        role_id INT IDENTITY(1,1) PRIMARY KEY,
        role_name NVARCHAR(50) NOT NULL UNIQUE,
        description NVARCHAR(255),
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP
    );
END
GO

-- 2. Users Table
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        user_id INT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(50) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        first_name NVARCHAR(50) NOT NULL,
        last_name NVARCHAR(50) NOT NULL,
        role_id INT NOT NULL,
        contact_no NVARCHAR(20),
        profile_pic NVARCHAR(255),
        status NVARCHAR(20) DEFAULT 'ACTIVE',
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES dbo.roles(role_id)
    );
END
GO

-- 3. Courses Table
IF OBJECT_ID(N'dbo.courses', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.courses (
        course_id INT IDENTITY(1,1) PRIMARY KEY,
        course_code NVARCHAR(20) NOT NULL UNIQUE,
        course_name NVARCHAR(150) NOT NULL,
        credit_value INT NOT NULL DEFAULT 2,
        credits INT NOT NULL DEFAULT 3,
        theory_hours INT DEFAULT 30,
        practical_hours INT DEFAULT 30,
        theory_sessions INT NOT NULL DEFAULT 15,
        practical_sessions INT NOT NULL DEFAULT 0,
        department NVARCHAR(100) NOT NULL,
        semester NVARCHAR(50) DEFAULT 'Semester 2',
        semester_number INT NOT NULL DEFAULT 1,
        academic_year NVARCHAR(20) NOT NULL DEFAULT '2025/2026',
        description NVARCHAR(MAX),
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP
    );
END
GO

-- 3a. Course Materials Table (Member 2)
IF OBJECT_ID(N'dbo.course_materials', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.course_materials (
        material_id INT IDENTITY(1,1) PRIMARY KEY,
        course_id INT NOT NULL,
        uploaded_by INT NOT NULL,
        title NVARCHAR(150) NOT NULL,
        file_path NVARCHAR(255) NOT NULL,
        uploaded_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT fk_materials_lecturer FOREIGN KEY (uploaded_by) REFERENCES dbo.users(user_id)
    );
END
GO

-- 3b. Course Lecturers Mapping Table (Member 2)
IF OBJECT_ID(N'dbo.course_lecturers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.course_lecturers (
        course_id INT NOT NULL,
        lecturer_id INT NOT NULL,
        PRIMARY KEY (course_id, lecturer_id),
        CONSTRAINT fk_cl_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT fk_cl_lecturer FOREIGN KEY (lecturer_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE
    );
END
GO

-- 3c. Evaluations Table (Member 2)
IF OBJECT_ID(N'dbo.evaluations', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.evaluations (
        evaluation_id INT IDENTITY(1,1) PRIMARY KEY,
        course_id INT NOT NULL,
        title NVARCHAR(100) NOT NULL,
        type NVARCHAR(20) NOT NULL,
        weightage FLOAT NOT NULL,
        max_marks FLOAT DEFAULT 100,
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_evaluations_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE
    );
END
GO

-- 4. Enrollments Table
IF OBJECT_ID(N'dbo.enrollments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.enrollments (
        enrollment_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NOT NULL,
        semester_number INT NOT NULL,
        academic_year NVARCHAR(20) NOT NULL,
        status NVARCHAR(20) DEFAULT 'ONGOING',
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT uq_enrollment UNIQUE (student_id, course_id, semester_number, academic_year)
    );
END
GO

-- 5. Unified Marks Table (Supports Member 2 evaluation marks and Member 4 component marks)
IF OBJECT_ID(N'dbo.marks', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.marks (
        mark_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NULL,
        evaluation_id INT NULL,
        marks_obtained DECIMAL(5,2) NULL,
        quiz_marks DECIMAL(5,2) NULL,
        assignment_marks DECIMAL(5,2) NULL,
        mid_sem_marks DECIMAL(5,2) NULL,
        practical_marks DECIMAL(5,2) NULL,
        final_exam_marks DECIMAL(5,2) NULL,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_marks_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT fk_marks_eval FOREIGN KEY (evaluation_id) REFERENCES dbo.evaluations(evaluation_id) ON DELETE CASCADE
    );
END
ELSE
BEGIN
    IF COL_LENGTH(N'dbo.marks', N'evaluation_id') IS NULL
        ALTER TABLE dbo.marks ADD evaluation_id INT NULL;
    IF COL_LENGTH(N'dbo.marks', N'marks_obtained') IS NULL
        ALTER TABLE dbo.marks ADD marks_obtained DECIMAL(5,2) NULL;
    IF COL_LENGTH(N'dbo.marks', N'course_id') IS NULL
        ALTER TABLE dbo.marks ADD course_id INT NULL;
    IF COL_LENGTH(N'dbo.marks', N'quiz_marks') IS NULL
        ALTER TABLE dbo.marks ADD quiz_marks DECIMAL(5,2) NULL;
    IF COL_LENGTH(N'dbo.marks', N'assignment_marks') IS NULL
        ALTER TABLE dbo.marks ADD assignment_marks DECIMAL(5,2) NULL;
    IF COL_LENGTH(N'dbo.marks', N'mid_sem_marks') IS NULL
        ALTER TABLE dbo.marks ADD mid_sem_marks DECIMAL(5,2) NULL;
    IF COL_LENGTH(N'dbo.marks', N'practical_marks') IS NULL
        ALTER TABLE dbo.marks ADD practical_marks DECIMAL(5,2) NULL;
    IF COL_LENGTH(N'dbo.marks', N'final_exam_marks') IS NULL
        ALTER TABLE dbo.marks ADD final_exam_marks DECIMAL(5,2) NULL;
END
GO

-- 5a. CA Eligibility Table (Member 2)
IF OBJECT_ID(N'dbo.eligibility', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.eligibility (
        eligibility_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NOT NULL,
        ca_percentage FLOAT NOT NULL,
        is_eligible BIT NOT NULL,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_eligibility_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_eligibility_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT uq_eligibility UNIQUE (student_id, course_id)
    );
END
GO

-- 6. Timetable Table
IF OBJECT_ID(N'dbo.timetable', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.timetable (
        timetable_id INT IDENTITY(1,1) PRIMARY KEY,
        course_id INT NOT NULL,
        day_of_week NVARCHAR(20) NOT NULL,
        start_time TIME NOT NULL,
        end_time TIME NOT NULL,
        session_type NVARCHAR(20) NOT NULL DEFAULT 'THEORY',
        lecturer_name NVARCHAR(100),
        venue NVARCHAR(100),
        semester_number INT NOT NULL DEFAULT 1,
        academic_year NVARCHAR(20) NOT NULL DEFAULT '2025/2026',
        CONSTRAINT fk_timetable_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE
    );
END
GO

-- 7. Grades Table
IF OBJECT_ID(N'dbo.grades', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.grades (
        grade_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NOT NULL,
        semester_number INT NOT NULL,
        academic_year NVARCHAR(20) NOT NULL,
        ca_marks DECIMAL(5,2) NOT NULL,
        final_exam_marks DECIMAL(5,2) NOT NULL,
        final_total_marks DECIMAL(5,2) NOT NULL,
        ca_eligible BIT NOT NULL DEFAULT 1,
        grade_letter NVARCHAR(5) NOT NULL,
        grade_point DECIMAL(3,2) NOT NULL,
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_grades_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_grades_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT uq_grade UNIQUE (student_id, course_id, semester_number, academic_year)
    );
END
GO

-- 8. Results Table
IF OBJECT_ID(N'dbo.results', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.results (
        result_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        semester_number INT NOT NULL,
        academic_year NVARCHAR(20) NOT NULL,
        sgpa DECIMAL(4,3) NOT NULL,
        credits_attempted INT NOT NULL,
        credits_completed INT NOT NULL,
        published_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_results_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT uq_result UNIQUE (student_id, semester_number, academic_year)
    );
END
GO

-- 8a. Attendance Sessions Table (Member 3)
IF OBJECT_ID(N'dbo.attendance_sessions', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.attendance_sessions (
        session_id INT IDENTITY(1,1) PRIMARY KEY,
        course_id NVARCHAR(20) NOT NULL,
        session_type NVARCHAR(20) NOT NULL,
        session_number INT DEFAULT 1,
        session_date DATE NOT NULL,
        hours DECIMAL(3,1) DEFAULT 2.0,
        created_by INT,
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_att_sess_user FOREIGN KEY (created_by) REFERENCES dbo.users(user_id) ON DELETE SET NULL
    );
END
GO

-- 9. Unified Attendance Records Table (Member 3 session records + Member 4 course summaries)
IF OBJECT_ID(N'dbo.attendance_records', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.attendance_records (
        attendance_id INT IDENTITY(1,1) PRIMARY KEY,
        record_id INT NULL,
        session_id INT NULL,
        student_id INT NOT NULL,
        course_id INT NULL,
        status NVARCHAR(20) DEFAULT 'PRESENT',
        theory_attended INT NOT NULL DEFAULT 0,
        theory_total INT NOT NULL DEFAULT 0,
        practical_attended INT NOT NULL DEFAULT 0,
        practical_total INT NOT NULL DEFAULT 0,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_attendance_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT fk_attendance_session FOREIGN KEY (session_id) REFERENCES dbo.attendance_sessions(session_id) ON DELETE CASCADE
    );
END
ELSE
BEGIN
    IF COL_LENGTH(N'dbo.attendance_records', N'session_id') IS NULL
        ALTER TABLE dbo.attendance_records ADD session_id INT NULL;
    IF COL_LENGTH(N'dbo.attendance_records', N'status') IS NULL
        ALTER TABLE dbo.attendance_records ADD status NVARCHAR(20) DEFAULT 'PRESENT';
    IF COL_LENGTH(N'dbo.attendance_records', N'record_id') IS NULL
        ALTER TABLE dbo.attendance_records ADD record_id INT NULL;
    IF COL_LENGTH(N'dbo.attendance_records', N'theory_attended') IS NULL
        ALTER TABLE dbo.attendance_records ADD theory_attended INT NOT NULL DEFAULT 0;
    IF COL_LENGTH(N'dbo.attendance_records', N'theory_total') IS NULL
        ALTER TABLE dbo.attendance_records ADD theory_total INT NOT NULL DEFAULT 0;
    IF COL_LENGTH(N'dbo.attendance_records', N'practical_attended') IS NULL
        ALTER TABLE dbo.attendance_records ADD practical_attended INT NOT NULL DEFAULT 0;
    IF COL_LENGTH(N'dbo.attendance_records', N'practical_total') IS NULL
        ALTER TABLE dbo.attendance_records ADD practical_total INT NOT NULL DEFAULT 0;
END
GO

-- 9a. Medical Records Table (Member 3)
IF OBJECT_ID(N'dbo.medical_records', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.medical_records (
        medical_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id NVARCHAR(20) NOT NULL,
        session_id INT NULL,
        medical_date DATE NOT NULL,
        reason NVARCHAR(MAX) NOT NULL,
        status NVARCHAR(20) DEFAULT 'PENDING',
        document_path NVARCHAR(255) NULL,
        submitted_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_med_rec_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_med_rec_session FOREIGN KEY (session_id) REFERENCES dbo.attendance_sessions(session_id) ON DELETE SET NULL
    );
END
GO

-- 10. Medical Submissions Table (Member 4)
IF OBJECT_ID(N'dbo.medical_submissions', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.medical_submissions (
        medical_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NULL,
        start_date DATE NOT NULL,
        end_date DATE NOT NULL,
        reason NVARCHAR(500) NOT NULL,
        document_path NVARCHAR(255),
        status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
        submitted_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_medical_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_medical_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE SET NULL
    );
END
GO

-- 11. Notices Table
IF OBJECT_ID(N'dbo.notices', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.notices (
        notice_id INT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(200) NOT NULL,
        content NVARCHAR(MAX) NOT NULL,
        category NVARCHAR(50) NOT NULL DEFAULT 'GENERAL',
        priority NVARCHAR(20) NOT NULL DEFAULT 'NORMAL',
        posted_by NVARCHAR(100) NOT NULL DEFAULT 'Faculty Administration',
        posted_date DATE NOT NULL,
        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP
    );
END
GO

-- ==============================================================================
-- SEED DATA
-- ==============================================================================

-- 1. Roles Seed
SET IDENTITY_INSERT dbo.roles ON;
MERGE INTO dbo.roles AS target
USING (VALUES 
    (1, N'ADMIN', N'System Administrator with full management privileges'),
    (2, N'LECTURER', N'Academic staff handling courses, marks, materials, and student progress'),
    (3, N'TECHNICAL_OFFICER', N'Technical Officer handling attendance, lab sessions, and medical submissions'),
    (4, N'UNDERGRADUATE', N'Enrolled student accessing attendance, courses, grades, notices, and timetables')
) AS source (role_id, role_name, description)
ON target.role_id = source.role_id
WHEN MATCHED THEN
    UPDATE SET role_name = source.role_name, description = source.description
WHEN NOT MATCHED THEN
    INSERT (role_id, role_name, description) VALUES (source.role_id, source.role_name, source.description);
SET IDENTITY_INSERT dbo.roles OFF;
GO

-- 2. Users Seed
SET IDENTITY_INSERT dbo.users ON;
MERGE INTO dbo.users AS target
USING (VALUES
    (1, N'admin', N'admin123', N'admin@fot.ruh.ac.lk', N'Admin', N'User', 1, N'0711234567', N'ACTIVE'),
    (2, N'lec_kamal', N'lec123', N'kamal@fot.ruh.ac.lk', N'Kamal', N'Perera', 2, N'0771122334', N'ACTIVE'),
    (3, N'lec_nimal', N'lec123', N'nimal@fot.ruh.ac.lk', N'Nimal', N'Silva', 2, N'0772233445', N'ACTIVE'),
    (4, N'lec_sunil', N'lec123', N'sunil@fot.ruh.ac.lk', N'Sunil', N'Fernando', 2, N'0773344556', N'ACTIVE'),
    (5, N'lec_amara', N'lec123', N'amara@fot.ruh.ac.lk', N'Amara', N'Jayasinghe', 2, N'0774455667', N'ACTIVE'),
    (6, N'lec_champa', N'lec123', N'champa@fot.ruh.ac.lk', N'Champa', N'Wickramasinghe', 2, N'0775566778', N'ACTIVE'),
    (7, N'to_bandara', N'to123', N'bandara@fot.ruh.ac.lk', N'Bandara', N'Herath', 3, N'0761122334', N'ACTIVE'),
    (8, N'to_sarath', N'to123', N'sarath@fot.ruh.ac.lk', N'Sarath', N'Kumara', 3, N'0762233445', N'ACTIVE'),
    (9, N'to_anura', N'to123', N'anura@fot.ruh.ac.lk', N'Anura', N'Dissanayake', 3, N'0763344556', N'ACTIVE'),
    (10, N'to_kasun', N'to123', N'kasun@fot.ruh.ac.lk', N'Kasun', N'Gunawardena', 3, N'0764455667', N'ACTIVE'),
    (11, N'tg2021001', N'std123', N'tg2021001@fot.ruh.ac.lk', N'Kasun', N'Kalhara', 4, N'0701122334', N'ACTIVE'),
    (12, N'tg2021002', N'std123', N'tg2021002@fot.ruh.ac.lk', N'Nuwan', N'Pradeep', 4, N'0702233445', N'ACTIVE'),
    (13, N'tg2021003', N'std123', N'tg2021003@fot.ruh.ac.lk', N'Dinuka', N'Madushan', 4, N'0703344556', N'ACTIVE'),
    (14, N'tg2021004', N'std123', N'tg2021004@fot.ruh.ac.lk', N'Sajith', N'Premadasa', 4, N'0704455667', N'ACTIVE'),
    (15, N'tg2021005', N'std123', N'tg2021005@fot.ruh.ac.lk', N'Ishara', N'Sandaruwan', 4, N'0705566778', N'ACTIVE'),
    (16, N'tg2021006', N'std123', N'tg2021006@fot.ruh.ac.lk', N'Tharindu', N'Bandara', 4, N'0795822412', N'ACTIVE'),
    (17, N'tg2021007', N'std123', N'tg2021007@fot.ruh.ac.lk', N'Chamodi', N'Weerasinghe', 4, N'0724942603', N'ACTIVE'),
    (18, N'tg2021008', N'std123', N'tg2021008@fot.ruh.ac.lk', N'Yasas', N'Jayawardena', 4, N'0713356886', N'ACTIVE'),
    (19, N'tg2021009', N'std123', N'tg2021009@fot.ruh.ac.lk', N'Hiruni', N'Rathnayake', 4, N'0746913810', N'ACTIVE'),
    (20, N'tg2021010', N'std123', N'tg2021010@fot.ruh.ac.lk', N'Dulaj', N'Abeywardena', 4, N'0742868828', N'ACTIVE'),
    (21, N'tg2021011', N'std123', N'tg2021011@fot.ruh.ac.lk', N'Sanduni', N'Wijesekara', 4, N'0739958838', N'ACTIVE'),
    (22, N'tg2021012', N'std123', N'tg2021012@fot.ruh.ac.lk', N'Ravindu', N'Gamage', 4, N'0728728463', N'ACTIVE'),
    (23, N'tg2021013', N'std123', N'tg2021013@fot.ruh.ac.lk', N'Vihanga', N'Senanayake', 4, N'0723756669', N'ACTIVE'),
    (24, N'tg2021014', N'std123', N'tg2021014@fot.ruh.ac.lk', N'Nethmi', N'Kularatne', 4, N'0783197857', N'ACTIVE'),
    (25, N'tg2021015', N'std123', N'tg2021015@fot.ruh.ac.lk', N'Oshadi', N'Athapattu', 4, N'0721668732', N'ACTIVE'),
    (26, N'tg2021016', N'std123', N'tg2021016@fot.ruh.ac.lk', N'Malith', N'Bandara', 4, N'0789254563', N'ACTIVE'),
    (27, N'tg2021017', N'std123', N'tg2021017@fot.ruh.ac.lk', N'Piumi', N'Weerasinghe', 4, N'0766629388', N'ACTIVE'),
    (28, N'tg2021018', N'std123', N'tg2021018@fot.ruh.ac.lk', N'Isuru', N'Jayawardena', 4, N'0714265799', N'ACTIVE'),
    (29, N'tg2021019', N'std123', N'tg2021019@fot.ruh.ac.lk', N'Kavindu', N'Rathnayake', 4, N'0713999315', N'ACTIVE'),
    (30, N'tg2021020', N'std123', N'tg2021020@fot.ruh.ac.lk', N'Sathmini', N'Abeywardena', 4, N'0722575562', N'ACTIVE')
) AS source (user_id, username, password, email, first_name, last_name, role_id, contact_no, status)
ON target.user_id = source.user_id
WHEN MATCHED THEN
    UPDATE SET password=source.password, email=source.email, first_name=source.first_name, last_name=source.last_name, role_id=source.role_id, contact_no=source.contact_no, status=source.status
WHEN NOT MATCHED THEN
    INSERT (user_id, username, password, email, first_name, last_name, role_id, contact_no, status)
    VALUES (source.user_id, source.username, source.password, source.email, source.first_name, source.last_name, source.role_id, source.contact_no, source.status);
SET IDENTITY_INSERT dbo.users OFF;
GO

-- 3. Courses Seed
MERGE INTO dbo.courses AS target
USING (VALUES
    (N'CS101', N'Programming Fundamentals', 3, 3, 30, 30, 15, 15, N'ICT', N'Semester 1', 1, N'2024/2025', N'Core programming concepts, syntax, control structures and functions'),
    (N'CS102', N'Computer Systems Architecture', 3, 3, 45, 0, 15, 0, N'ICT', N'Semester 1', 1, N'2024/2025', N'Digital logic, processor design, memory hierarchy and assembly'),
    (N'CS103', N'Web Technologies', 3, 3, 30, 30, 15, 15, N'ICT', N'Semester 1', 1, N'2024/2025', N'HTML, CSS, JavaScript, backend development and REST APIs'),
    (N'MA101', N'Mathematics for Computing I', 2, 2, 30, 0, 15, 0, N'ICT', N'Semester 1', 1, N'2024/2025', N'Calculus, matrices, linear algebra and computing applications'),
    (N'EN101', N'Communication Skills', 2, 2, 30, 0, 15, 0, N'ICT', N'Semester 1', 1, N'2024/2025', N'Professional technical writing, presentations and reporting'),
    (N'CS201', N'Data Structures & Algorithms', 4, 4, 30, 30, 15, 15, N'ICT', N'Semester 2', 2, N'2025/2026', N'Core algorithms, abstract data types, trees, graphs, and complexity analysis'),
    (N'CS202', N'Object Oriented Programming', 3, 3, 30, 30, 15, 15, N'ICT', N'Semester 2', 2, N'2025/2026', N'Comprehensive hands-on OOP software development using Java'),
    (N'CS203', N'Database Management Systems', 3, 3, 30, 30, 15, 15, N'ICT', N'Semester 2', 2, N'2025/2026', N'Relational data modeling, SQL programming, normalization, and indexing'),
    (N'CS204', N'Computer Networks', 3, 3, 45, 0, 15, 0, N'ICT', N'Semester 2', 2, N'2025/2026', N'OSI model, TCP/IP, network routing, switching, and security'),
    (N'MA201', N'Discrete Mathematics', 2, 2, 30, 0, 15, 0, N'ICT', N'Semester 2', 2, N'2025/2026', N'Set theory, logic, graph theory, combinatorics and automata')
) AS source (course_code, course_name, credit_value, credits, theory_hours, practical_hours, theory_sessions, practical_sessions, department, semester, semester_number, academic_year, description)
ON target.course_code = source.course_code
WHEN MATCHED THEN
    UPDATE SET course_name = source.course_name, credits = source.credits, credit_value = source.credit_value, description = source.description
WHEN NOT MATCHED THEN
    INSERT (course_code, course_name, credit_value, credits, theory_hours, practical_hours, theory_sessions, practical_sessions, department, semester, semester_number, academic_year, description)
    VALUES (source.course_code, source.course_name, source.credit_value, source.credits, source.theory_hours, source.practical_hours, source.theory_sessions, source.practical_sessions, source.department, source.semester, source.semester_number, source.academic_year, source.description);
GO

-- 3a. Course Lecturers Seed
MERGE INTO dbo.course_lecturers AS target
USING (VALUES
    (1, 2), (2, 2), (3, 3), (4, 4), (5, 5), (6, 2), (7, 5), (8, 3), (9, 4), (10, 6)
) AS source (course_id, lecturer_id)
ON target.course_id = source.course_id AND target.lecturer_id = source.lecturer_id
WHEN NOT MATCHED THEN
    INSERT (course_id, lecturer_id) VALUES (source.course_id, source.lecturer_id);
GO

-- 3b. Course Materials Seed
IF NOT EXISTS (SELECT 1 FROM dbo.course_materials)
BEGIN
    INSERT INTO dbo.course_materials (course_id, uploaded_by, title, file_path) VALUES
    (6, 2, N'Lecture 01 - Algorithm Analysis & Big-O Notation', N'resources/course_materials/CS201_Lecture01.pdf'),
    (6, 2, N'Tutorial 01 - Linked Lists & Stacks Problem Set', N'resources/course_materials/CS201_Tutorial01.pdf'),
    (7, 5, N'Practicum Guide - Swing GUI & MVC Architecture', N'resources/course_materials/CS202_SwingGuide.pdf'),
    (8, 3, N'Lecture 01 - Relational Algebra & Normalization', N'resources/course_materials/CS203_Lecture01.pdf'),
    (8, 3, N'Lab Sheet 01 - Complex SQL Queries & Joins', N'resources/course_materials/CS203_Lab01.pdf');
END
GO

-- 3c. Evaluations Seed
SET IDENTITY_INSERT dbo.evaluations ON;
MERGE INTO dbo.evaluations AS target
USING (VALUES
    (1, 6, N'Quiz 1 - Stacks and Queues', N'QUIZ', 10.0, 100.0),
    (2, 6, N'Continuous Assessment 1', N'ASSESSMENT', 15.0, 100.0),
    (3, 6, N'Mid-Semester Theory Examination', N'MID_TERM', 25.0, 100.0),
    (4, 6, N'Practical Lab Test', N'PRACTICAL', 20.0, 100.0),
    (5, 6, N'End-Semester Final Examination', N'FINAL', 30.0, 100.0),
    (6, 7, N'OOP Quiz - Inheritance & Polymorphism', N'QUIZ', 10.0, 100.0),
    (7, 7, N'Mini Project Milestone 1', N'ASSESSMENT', 20.0, 100.0),
    (8, 7, N'Mid Practicum Exam', N'MID_TERM', 20.0, 100.0),
    (9, 7, N'Final Project & Viva', N'FINAL', 50.0, 100.0)
) AS source (evaluation_id, course_id, title, type, weightage, max_marks)
ON target.evaluation_id = source.evaluation_id
WHEN MATCHED THEN
    UPDATE SET title = source.title, weightage = source.weightage
WHEN NOT MATCHED THEN
    INSERT (evaluation_id, course_id, title, type, weightage, max_marks)
    VALUES (source.evaluation_id, source.course_id, source.title, source.type, source.weightage, source.max_marks);
SET IDENTITY_INSERT dbo.evaluations OFF;
GO

-- 4. Enrollments Seed (20 students x 10 courses)
MERGE INTO dbo.enrollments AS target
USING (
    SELECT u.user_id, c.course_id, c.semester_number, c.academic_year,
           CASE WHEN c.semester_number = 1 THEN N'COMPLETED' ELSE N'ONGOING' END AS status
    FROM dbo.users u CROSS JOIN dbo.courses c
    WHERE u.user_id BETWEEN 11 AND 30
) AS source
ON target.student_id = source.user_id AND target.course_id = source.course_id AND target.semester_number = source.semester_number AND target.academic_year = source.academic_year
WHEN MATCHED THEN
    UPDATE SET status = source.status
WHEN NOT MATCHED THEN
    INSERT (student_id, course_id, semester_number, academic_year, status)
    VALUES (source.user_id, source.course_id, source.semester_number, source.academic_year, source.status);
GO

-- 5. Timetable Seed
MERGE INTO dbo.timetable AS target
USING (
    SELECT c.course_id, t.day_of_week, t.start_time, t.end_time, t.session_type, t.lecturer_name, t.venue, 2 AS semester_number, N'2025/2026' AS academic_year
    FROM (VALUES
        (N'CS201', N'MONDAY', CAST('08:00:00' AS TIME), CAST('10:00:00' AS TIME), N'THEORY', N'Mr. Kamal Perera', N'Lecture Hall A1'),
        (N'CS201', N'WEDNESDAY', CAST('13:00:00' AS TIME), CAST('15:00:00' AS TIME), N'PRACTICAL', N'Mr. Kamal Perera', N'Computer Lab 1'),
        (N'CS202', N'MONDAY', CAST('10:00:00' AS TIME), CAST('12:00:00' AS TIME), N'THEORY', N'Ms. Amara Jayasinghe', N'Lecture Hall A2'),
        (N'CS202', N'THURSDAY', CAST('09:00:00' AS TIME), CAST('11:00:00' AS TIME), N'PRACTICAL', N'Ms. Amara Jayasinghe', N'Computer Lab 2'),
        (N'CS203', N'TUESDAY', CAST('08:00:00' AS TIME), CAST('10:00:00' AS TIME), N'THEORY', N'Mr. Nimal Silva', N'Lecture Hall B1'),
        (N'CS203', N'FRIDAY', CAST('13:00:00' AS TIME), CAST('15:00:00' AS TIME), N'PRACTICAL', N'Mr. Nimal Silva', N'Computer Lab 1'),
        (N'CS204', N'TUESDAY', CAST('10:00:00' AS TIME), CAST('12:00:00' AS TIME), N'THEORY', N'Mr. Sunil Fernando', N'Lecture Hall B2'),
        (N'MA201', N'WEDNESDAY', CAST('08:00:00' AS TIME), CAST('10:00:00' AS TIME), N'THEORY', N'Ms. Champa Wickramasinghe', N'Lecture Hall A1')
    ) AS t (course_code, day_of_week, start_time, end_time, session_type, lecturer_name, venue)
    JOIN dbo.courses c ON c.course_code = t.course_code
) AS source
ON target.course_id = source.course_id AND target.day_of_week = source.day_of_week AND target.start_time = source.start_time
WHEN MATCHED THEN
    UPDATE SET lecturer_name = source.lecturer_name, venue = source.venue
WHEN NOT MATCHED THEN
    INSERT (course_id, day_of_week, start_time, end_time, session_type, lecturer_name, venue, semester_number, academic_year)
    VALUES (source.course_id, source.day_of_week, source.start_time, source.end_time, source.session_type, source.lecturer_name, source.venue, source.semester_number, source.academic_year);
GO

-- 6. Marks Seed (Component Marks + Evaluation Marks)
IF NOT EXISTS (SELECT 1 FROM dbo.marks)
BEGIN
    -- Seed component marks for students 11 to 30
    INSERT INTO dbo.marks (student_id, course_id, quiz_marks, assignment_marks, mid_sem_marks, practical_marks, final_exam_marks)
    SELECT e.student_id, e.course_id,
           CAST(50 + (e.student_id * 3 + e.course_id * 5) % 45 AS DECIMAL(5,2)),
           CAST(55 + (e.student_id * 4 + e.course_id * 3) % 40 AS DECIMAL(5,2)),
           CAST(45 + (e.student_id * 2 + e.course_id * 7) % 50 AS DECIMAL(5,2)),
           CAST(60 + (e.student_id * 5 + e.course_id * 2) % 35 AS DECIMAL(5,2)),
           CAST(50 + (e.student_id * 7 + e.course_id * 4) % 45 AS DECIMAL(5,2))
    FROM dbo.enrollments e;

    -- Seed evaluation-specific marks for Member 2 UI demonstration
    INSERT INTO dbo.marks (student_id, evaluation_id, marks_obtained) VALUES
    (11, 1, 85.0), (11, 2, 78.0), (11, 3, 72.0), (11, 4, 80.0), (11, 5, 88.0),
    (12, 1, 65.0), (12, 2, 60.0), (12, 3, 58.0), (12, 4, 70.0), (12, 5, 62.0),
    (13, 1, 45.0), (13, 2, 42.0), (13, 3, 40.0), (13, 4, 48.0), (13, 5, 50.0),
    (14, 1, 28.0), (14, 2, 32.0), (14, 3, 25.0), (14, 4, 30.0), (14, 5, 35.0),
    (15, 1, 92.0), (15, 2, 88.0), (15, 3, 85.0), (15, 4, 90.0), (15, 5, 86.0);
END
GO

-- 6a. Eligibility Seed (Member 2)
IF NOT EXISTS (SELECT 1 FROM dbo.eligibility)
BEGIN
    INSERT INTO dbo.eligibility (student_id, course_id, ca_percentage, is_eligible) VALUES
    (11, 6, 78.75, 1),
    (12, 6, 63.25, 1),
    (13, 6, 43.75, 1),
    (14, 6, 28.75, 0),
    (15, 6, 88.75, 1),
    (11, 8, 82.00, 1),
    (12, 8, 67.50, 1),
    (14, 8, 34.00, 0);
END
GO

-- 7. Attendance Sessions Seed (Member 3)
IF NOT EXISTS (SELECT 1 FROM dbo.attendance_sessions)
BEGIN
    INSERT INTO dbo.attendance_sessions (course_id, session_type, session_number, session_date, hours, created_by) VALUES
    (N'CS201', N'THEORY', 1, '2026-06-08', 2.0, 7),
    (N'CS201', N'THEORY', 2, '2026-06-15', 2.0, 7),
    (N'CS201', N'THEORY', 3, '2026-06-22', 2.0, 7),
    (N'CS201', N'THEORY', 4, '2026-06-29', 2.0, 7),
    (N'CS201', N'THEORY', 5, '2026-07-06', 2.0, 7),
    (N'CS201', N'THEORY', 6, '2026-07-13', 2.0, 7),
    (N'CS201', N'THEORY', 7, '2026-07-20', 2.0, 7),
    (N'CS201', N'THEORY', 8, '2026-07-27', 2.0, 7),
    (N'CS201', N'THEORY', 9, '2026-08-03', 2.0, 7),
    (N'CS201', N'THEORY', 10, '2026-08-10', 2.0, 7),
    (N'CS201', N'THEORY', 11, '2026-08-17', 2.0, 7),
    (N'CS201', N'THEORY', 12, '2026-08-24', 2.0, 7),
    (N'CS201', N'THEORY', 13, '2026-08-31', 2.0, 7),
    (N'CS201', N'THEORY', 14, '2026-09-07', 2.0, 7),
    (N'CS201', N'THEORY', 15, '2026-09-14', 2.0, 7),
    (N'CS201', N'PRACTICAL', 1, '2026-06-10', 2.0, 7),
    (N'CS201', N'PRACTICAL', 2, '2026-06-17', 2.0, 7),
    (N'CS201', N'PRACTICAL', 3, '2026-06-24', 2.0, 7),
    (N'CS201', N'PRACTICAL', 4, '2026-07-01', 2.0, 7),
    (N'CS201', N'PRACTICAL', 5, '2026-07-08', 2.0, 7),
    (N'CS201', N'PRACTICAL', 6, '2026-07-15', 2.0, 7),
    (N'CS201', N'PRACTICAL', 7, '2026-07-22', 2.0, 7),
    (N'CS201', N'PRACTICAL', 8, '2026-07-29', 2.0, 7),
    (N'CS201', N'PRACTICAL', 9, '2026-08-05', 2.0, 7),
    (N'CS201', N'PRACTICAL', 10, '2026-08-12', 2.0, 7),
    (N'CS201', N'PRACTICAL', 11, '2026-08-19', 2.0, 7),
    (N'CS201', N'PRACTICAL', 12, '2026-08-26', 2.0, 7),
    (N'CS201', N'PRACTICAL', 13, '2026-09-02', 2.0, 7),
    (N'CS201', N'PRACTICAL', 14, '2026-09-09', 2.0, 7),
    (N'CS201', N'PRACTICAL', 15, '2026-09-16', 2.0, 7);
END
GO

-- 8. Attendance Summary & Session Records Seed
IF NOT EXISTS (SELECT 1 FROM dbo.attendance_records WHERE course_id IS NOT NULL)
BEGIN
    INSERT INTO dbo.attendance_records (student_id, course_id, theory_attended, theory_total, practical_attended, practical_total)
    SELECT e.student_id, e.course_id,
           CASE WHEN c.theory_sessions >= 2 THEN c.theory_sessions - 2 ELSE 0 END,
           c.theory_sessions,
           CASE WHEN c.practical_sessions >= 1 THEN c.practical_sessions - 1 ELSE 0 END,
           c.practical_sessions
    FROM dbo.enrollments e JOIN dbo.courses c ON c.course_id = e.course_id;
END
GO

-- Session-level attendance records
IF NOT EXISTS (SELECT 1 FROM dbo.attendance_records WHERE session_id IS NOT NULL)
BEGIN
    INSERT INTO dbo.attendance_records (session_id, student_id, status)
    SELECT s.session_id, u.user_id,
           CASE 
               WHEN u.user_id IN (11, 12) THEN N'PRESENT'
               WHEN u.user_id IN (13, 14) AND s.session_number = 15 THEN N'ABSENT'
               WHEN u.user_id IN (15, 16, 17) AND s.session_number > 12 THEN N'ABSENT'
               WHEN u.user_id IN (18, 19, 20, 21) AND s.session_number > 9 THEN N'ABSENT'
               WHEN u.user_id IN (22, 23, 24) AND s.session_number > 13 THEN N'ABSENT'
               WHEN u.user_id IN (25, 26, 27) AND s.session_number > 10 THEN N'ABSENT'
               WHEN u.user_id IN (28, 29, 30) AND s.session_number > 7 THEN N'ABSENT'
               ELSE N'PRESENT'
           END
    FROM dbo.attendance_sessions s CROSS JOIN dbo.users u
    WHERE u.user_id BETWEEN 11 AND 30;
END
GO

-- 9. Medical Records Seed (Member 3)
IF NOT EXISTS (SELECT 1 FROM dbo.medical_records)
BEGIN
    INSERT INTO dbo.medical_records (student_id, course_id, session_id, medical_date, reason, status) VALUES
    (22, N'CS201', 14, '2026-09-07', N'Acute tonsillitis - Treated at General Hospital', N'APPROVED'),
    (23, N'CS201', 15, '2026-09-14', N'Severe tooth extraction recovery and pain management', N'APPROVED'),
    (24, N'CS201', 15, '2026-09-14', N'Sprained ankle during inter-faculty tournament', N'APPROVED'),
    (25, N'CS201', 11, '2026-08-17', N'Viral influenza with high fever', N'APPROVED'),
    (25, N'CS201', 12, '2026-08-24', N'Post-viral asthenia and convalescence', N'APPROVED'),
    (25, N'CS201', 13, '2026-08-31', N'Follow-up clinical examination', N'APPROVED'),
    (28, N'CS201', 13, '2026-08-31', N'Severe allergic reaction - Under review', N'PENDING'),
    (29, N'CS201', 10, '2026-08-10', N'Private clinic prescription without doctor stamp', N'REJECTED');
END
GO

-- 10. Medical Submissions Seed (Member 4)
IF NOT EXISTS (SELECT 1 FROM dbo.medical_submissions)
BEGIN
    INSERT INTO dbo.medical_submissions (student_id, course_id, start_date, end_date, reason, document_path, status) VALUES
    (11, 6, '2026-08-01', '2026-08-05', N'High fever and physician-advised rest', N'uploads/medical/std11_cert.pdf', N'APPROVED'),
    (12, 6, '2026-08-10', '2026-08-12', N'Food poisoning and outpatient hospital care', N'uploads/medical/std12_cert.pdf', N'APPROVED'),
    (13, 7, '2026-08-15', '2026-08-18', N'Dental extraction and post-procedure recovery', N'uploads/medical/std13_cert.pdf', N'PENDING'),
    (14, 8, '2026-08-20', '2026-08-22', N'Migraine aura episode requiring clinic observation', N'uploads/medical/std14_cert.pdf', N'REJECTED');
END
GO

-- 11. Grades Seed (Member 4)
IF NOT EXISTS (SELECT 1 FROM dbo.grades)
BEGIN
    INSERT INTO dbo.grades (student_id, course_id, semester_number, academic_year, ca_marks, final_exam_marks, final_total_marks, ca_eligible, grade_letter, grade_point)
    SELECT e.student_id, e.course_id, e.semester_number, e.academic_year,
           CAST(65.0 + (e.student_id * 2) % 30 AS DECIMAL(5,2)),
           CAST(60.0 + (e.student_id * 3) % 35 AS DECIMAL(5,2)),
           CAST(62.0 + (e.student_id * 2.5) % 32 AS DECIMAL(5,2)),
           1,
           CASE 
               WHEN (62 + (e.student_id * 2.5) % 32) >= 80 THEN N'A+'
               WHEN (62 + (e.student_id * 2.5) % 32) >= 75 THEN N'A'
               WHEN (62 + (e.student_id * 2.5) % 32) >= 70 THEN N'A-'
               WHEN (62 + (e.student_id * 2.5) % 32) >= 65 THEN N'B+'
               WHEN (62 + (e.student_id * 2.5) % 32) >= 60 THEN N'B'
               ELSE N'C+'
           END,
           CASE 
               WHEN (62 + (e.student_id * 2.5) % 32) >= 80 THEN 4.00
               WHEN (62 + (e.student_id * 2.5) % 32) >= 75 THEN 4.00
               WHEN (62 + (e.student_id * 2.5) % 32) >= 70 THEN 3.70
               WHEN (62 + (e.student_id * 2.5) % 32) >= 65 THEN 3.30
               WHEN (62 + (e.student_id * 2.5) % 32) >= 60 THEN 3.00
               ELSE 2.30
           END
    FROM dbo.enrollments e
    WHERE e.semester_number = 1;
END
GO

-- 12. Results Seed (Member 4)
IF NOT EXISTS (SELECT 1 FROM dbo.results)
BEGIN
    INSERT INTO dbo.results (student_id, semester_number, academic_year, sgpa, credits_attempted, credits_completed)
    SELECT u.user_id, 1, N'2024/2025',
           CAST(3.000 + ((u.user_id * 7) % 100) / 100.0 AS DECIMAL(4,3)),
           13, 13
    FROM dbo.users u
    WHERE u.user_id BETWEEN 11 AND 30;
END
GO

-- 13. Notices Seed
IF NOT EXISTS (SELECT 1 FROM dbo.notices)
BEGIN
    INSERT INTO dbo.notices (title, content, category, priority, posted_by, posted_date)
    VALUES
    (N'Mandatory 80% Attendance Requirement for Semester 2 Examinations',
     N'All undergraduates of the Faculty of Technology are hereby reminded that an overall attendance of at least 80% in both theory lectures and laboratory sessions is mandatory to be deemed eligible to sit for the upcoming semester final examinations. Students falling below 80% must immediately verify that all medical certificates have been formally lodged with the Technical Officer.',
     N'ACADEMIC', N'URGENT', N'Department of Information and Communication Technology', CAST(GETDATE() - 1 AS DATE)),
    (N'Continuous Assessment (CA) Verification Period Open',
     N'The continuous assessment marks including Quizzes, Practical assessments, and Mid-Semester evaluations have been entered into the system. Students are advised to verify their marks from the student portal and report any discrepancies to the respective Course Coordinator before the end of the week.',
     N'EXAMINATION', N'NORMAL', N'Faculty Examination Unit', CAST(GETDATE() - 3 AS DATE)),
    (N'Submission of Medical Certificates for Missed Practical Sessions',
     N'Undergraduates who have missed scheduled laboratory practical sessions due to illness or approved medical emergencies must submit official Government Hospital medical certificates within 14 days of absence to the Technical Officer for departmental board approval.',
     N'GENERAL', N'NORMAL', N'Office of the Dean, Faculty of Technology', CAST(GETDATE() - 7 AS DATE));
END
GO
