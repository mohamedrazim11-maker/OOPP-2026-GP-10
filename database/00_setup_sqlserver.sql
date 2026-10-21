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

-- 5. Marks Table
IF OBJECT_ID(N'dbo.marks', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.marks (
        mark_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NOT NULL,
        quiz_marks DECIMAL(5,2) NULL,
        assignment_marks DECIMAL(5,2) NULL,
        mid_sem_marks DECIMAL(5,2) NULL,
        practical_marks DECIMAL(5,2) NULL,
        final_exam_marks DECIMAL(5,2) NULL,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_marks_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT uq_marks UNIQUE (student_id, course_id)
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

-- 9. Attendance Records Table
IF OBJECT_ID(N'dbo.attendance_records', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.attendance_records (
        attendance_id INT IDENTITY(1,1) PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NOT NULL,
        theory_attended INT NOT NULL DEFAULT 0,
        theory_total INT NOT NULL DEFAULT 0,
        practical_attended INT NOT NULL DEFAULT 0,
        practical_total INT NOT NULL DEFAULT 0,
        updated_at DATETIME2 DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
        CONSTRAINT fk_attendance_course FOREIGN KEY (course_id) REFERENCES dbo.courses(course_id) ON DELETE CASCADE,
        CONSTRAINT uq_attendance UNIQUE (student_id, course_id)
    );
END
GO

-- 10. Medical Submissions Table
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

-- 6. Attendance Summary Seed
MERGE INTO dbo.attendance_records AS target
USING (
    SELECT e.student_id, e.course_id,
           CASE WHEN c.theory_sessions >= 2 THEN c.theory_sessions - 2 ELSE 0 END AS theory_attended,
           c.theory_sessions AS theory_total,
           CASE WHEN c.practical_sessions >= 1 THEN c.practical_sessions - 1 ELSE 0 END AS practical_attended,
           c.practical_sessions AS practical_total
    FROM dbo.enrollments e JOIN dbo.courses c ON c.course_id = e.course_id
) AS source
ON target.student_id = source.student_id AND target.course_id = source.course_id
WHEN MATCHED THEN
    UPDATE SET theory_attended = source.theory_attended, theory_total = source.theory_total, practical_attended = source.practical_attended, practical_total = source.practical_total
WHEN NOT MATCHED THEN
    INSERT (student_id, course_id, theory_attended, theory_total, practical_attended, practical_total)
    VALUES (source.student_id, source.course_id, source.theory_attended, source.theory_total, source.practical_attended, source.practical_total);
GO

-- 7. Notices Seed
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
