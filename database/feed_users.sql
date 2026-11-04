-- ==============================================================================
-- Feed Script: 3 Targeted Roles (Lecturer Aysha, Student Rashkan, TO Hansika)
-- Faculty Management System | University of Ruhuna - Faculty of Technology
-- ==============================================================================

USE [faculty_management_db];
GO

SET ANSI_NULLS ON;
GO
SET QUOTED_IDENTIFIER ON;
GO

PRINT '>>> Creating / Updating the 3 Users: Aysha, Rashkan, Hansika...';

-- 1. Ensure Roles exist
IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_id = 2)
    INSERT INTO dbo.roles (role_id, role_name, description) VALUES (2, N'LECTURER', N'Academic Staff');
IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_id = 3)
    INSERT INTO dbo.roles (role_id, role_name, description) VALUES (3, N'TECHNICAL_OFFICER', N'Technical Officer');
IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_id = 4)
    INSERT INTO dbo.roles (role_id, role_name, description) VALUES (4, N'STUDENT', N'Undergraduate Student');
GO

-- 2. Insert / Update the 3 Users
-- Aysha (Lecturer, role_id = 2, password = 123456)
IF EXISTS (SELECT 1 FROM dbo.users WHERE UPPER(username) = UPPER('Aysha'))
BEGIN
    UPDATE dbo.users 
    SET password = N'123456', role_id = 2, first_name = N'Aysha', last_name = N'Ahamed',
        email = N'aysha@fot.ruh.ac.lk', contact_no = N'0779988776', status = N'ACTIVE'
    WHERE UPPER(username) = UPPER('Aysha');
END
ELSE
BEGIN
    INSERT INTO dbo.users (username, password, email, first_name, last_name, role_id, contact_no, profile_pic, status)
    VALUES (N'Aysha', N'123456', N'aysha@fot.ruh.ac.lk', N'Aysha', N'Ahamed', 2, N'0779988776', NULL, N'ACTIVE');
END

-- Rashkan (Undergraduate, role_id = 4, password = 123456)
IF EXISTS (SELECT 1 FROM dbo.users WHERE UPPER(username) = UPPER('Rashkan'))
BEGIN
    UPDATE dbo.users 
    SET password = N'123456', role_id = 4, first_name = N'Mohamed', last_name = N'Rashkan',
        email = N'rashkan@fot.ruh.ac.lk', contact_no = N'0707788990', status = N'ACTIVE'
    WHERE UPPER(username) = UPPER('Rashkan');
END
ELSE
BEGIN
    INSERT INTO dbo.users (username, password, email, first_name, last_name, role_id, contact_no, profile_pic, status)
    VALUES (N'Rashkan', N'123456', N'rashkan@fot.ruh.ac.lk', N'Mohamed', N'Rashkan', 4, N'0707788990', NULL, N'ACTIVE');
END

-- Hansika (Technical Officer, role_id = 3, password = 123456)
IF EXISTS (SELECT 1 FROM dbo.users WHERE UPPER(username) = UPPER('Hansika'))
BEGIN
    UPDATE dbo.users 
    SET password = N'123456', role_id = 3, first_name = N'Hansika', last_name = N'Perera',
        email = N'hansika@fot.ruh.ac.lk', contact_no = N'0765544332', status = N'ACTIVE'
    WHERE UPPER(username) = UPPER('Hansika');
END
ELSE
BEGIN
    INSERT INTO dbo.users (username, password, email, first_name, last_name, role_id, contact_no, profile_pic, status)
    VALUES (N'Hansika', N'123456', N'hansika@fot.ruh.ac.lk', N'Hansika', N'Perera', 3, N'0765544332', NULL, N'ACTIVE');
END
GO

-- 3. Resolve User IDs and Courses
DECLARE @aysha_id INT, @rashkan_id INT, @hansika_id INT;
SELECT @aysha_id = user_id FROM dbo.users WHERE UPPER(username) = UPPER('Aysha');
SELECT @rashkan_id = user_id FROM dbo.users WHERE UPPER(username) = UPPER('Rashkan');
SELECT @hansika_id = user_id FROM dbo.users WHERE UPPER(username) = UPPER('Hansika');

PRINT 'Resolved User IDs:';
PRINT '  Aysha (Lecturer): ' + CAST(@aysha_id AS VARCHAR(10));
PRINT '  Rashkan (Student): ' + CAST(@rashkan_id AS VARCHAR(10));
PRINT '  Hansika (Tech Officer): ' + CAST(@hansika_id AS VARCHAR(10));

-- Resolve Key Courses: ICT2112, ICT2123, ICT2132, CS201, CS202, CS203
DECLARE @c_ict2112 INT, @c_ict2123 INT, @c_ict2132 INT, @c_cs201 INT, @c_cs202 INT, @c_cs203 INT;
SELECT @c_ict2112 = course_id FROM dbo.courses WHERE course_code = 'ICT2112';
SELECT @c_ict2123 = course_id FROM dbo.courses WHERE course_code = 'ICT2123';
SELECT @c_ict2132 = course_id FROM dbo.courses WHERE course_code = 'ICT2132';
SELECT @c_cs201 = course_id FROM dbo.courses WHERE course_code = 'CS201';
SELECT @c_cs202 = course_id FROM dbo.courses WHERE course_code = 'CS202';
SELECT @c_cs203 = course_id FROM dbo.courses WHERE course_code = 'CS203';

-- Fallback creation if any course is missing
IF @c_ict2112 IS NULL
BEGIN
    INSERT INTO dbo.courses (course_code, course_name, credit_value, credits, theory_hours, practical_hours, theory_sessions, practical_sessions, department, semester, semester_number, academic_year, description)
    VALUES (N'ICT2112', N'Data Structures and Algorithms', 2, 2, 30, 30, 15, 15, N'Information & Communication Technology', N'Semester 1', 1, N'2025/2026', N'Core algorithm principles');
    SET @c_ict2112 = SCOPE_IDENTITY();
END
IF @c_ict2123 IS NULL
BEGIN
    INSERT INTO dbo.courses (course_code, course_name, credit_value, credits, theory_hours, practical_hours, theory_sessions, practical_sessions, department, semester, semester_number, academic_year, description)
    VALUES (N'ICT2123', N'Database Management Systems', 3, 3, 30, 30, 15, 15, N'Information & Communication Technology', N'Semester 1', 1, N'2025/2026', N'Relational databases and SQL');
    SET @c_ict2123 = SCOPE_IDENTITY();
END
IF @c_ict2132 IS NULL
BEGIN
    INSERT INTO dbo.courses (course_code, course_name, credit_value, credits, theory_hours, practical_hours, theory_sessions, practical_sessions, department, semester, semester_number, academic_year, description)
    VALUES (N'ICT2132', N'Object Oriented Programming Practicum', 2, 2, 15, 45, 15, 15, N'Information & Communication Technology', N'Semester 2', 2, N'2025/2026', N'Java OOP and MVC architecture');
    SET @c_ict2132 = SCOPE_IDENTITY();
END

-- ==============================================================================
-- PART A: DATA FOR AYSHA (LECTURER)
-- ==============================================================================
PRINT '>>> Feeding Lecturer Aysha Data...';

-- A.1 Course Assignments (Aysha teaches ICT2112, ICT2123, ICT2132)
IF NOT EXISTS (SELECT 1 FROM dbo.course_lecturers WHERE course_id = @c_ict2112 AND lecturer_id = @aysha_id)
    INSERT INTO dbo.course_lecturers (course_id, lecturer_id) VALUES (@c_ict2112, @aysha_id);

IF NOT EXISTS (SELECT 1 FROM dbo.course_lecturers WHERE course_id = @c_ict2123 AND lecturer_id = @aysha_id)
    INSERT INTO dbo.course_lecturers (course_id, lecturer_id) VALUES (@c_ict2123, @aysha_id);

IF NOT EXISTS (SELECT 1 FROM dbo.course_lecturers WHERE course_id = @c_ict2132 AND lecturer_id = @aysha_id)
    INSERT INTO dbo.course_lecturers (course_id, lecturer_id) VALUES (@c_ict2132, @aysha_id);

IF @c_cs202 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.course_lecturers WHERE course_id = @c_cs202 AND lecturer_id = @aysha_id)
    INSERT INTO dbo.course_lecturers (course_id, lecturer_id) VALUES (@c_cs202, @aysha_id);

-- A.2 Course Materials Uploaded by Aysha
DELETE FROM dbo.course_materials WHERE uploaded_by = @aysha_id;

INSERT INTO dbo.course_materials (course_id, uploaded_by, title, file_path) VALUES
(@c_ict2112, @aysha_id, N'Lecture 01 - Asymptotic Complexity & Big-O Notation', N'resources/course_materials/ICT2112_Lecture01.pdf'),
(@c_ict2112, @aysha_id, N'Lecture 02 - Linear Data Structures: Linked Lists & Stacks', N'resources/course_materials/ICT2112_Lecture02.pdf'),
(@c_ict2112, @aysha_id, N'Tutorial 01 - Binary Trees and Tree Traversals Problem Set', N'resources/course_materials/ICT2112_Tutorial01.pdf'),
(@c_ict2123, @aysha_id, N'Lecture 01 - Relational Schema Design & Normalization (1NF-BCNF)', N'resources/course_materials/ICT2123_Lecture01.pdf'),
(@c_ict2123, @aysha_id, N'Lab Sheet 01 - Advanced SQL Joins, Subqueries & Transactions', N'resources/course_materials/ICT2123_Lab01.pdf'),
(@c_ict2132, @aysha_id, N'Practicum Guide - Java Swing MVC GUI Architecture & Event Dispatch', N'resources/course_materials/ICT2132_SwingMVC.pdf'),
(@c_ict2132, @aysha_id, N'Lab Sheet 02 - JDBC Connection Management & DAO Design Pattern', N'resources/course_materials/ICT2132_Lab02_DAO.pdf');

-- A.3 Evaluations for Courses Taught by Aysha
-- ICT2112 Evaluations
DECLARE @e_quiz1 INT, @e_ca1 INT, @e_mid INT, @e_prac INT, @e_final INT;

SELECT @e_quiz1 = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2112 AND type = 'QUIZ';
IF @e_quiz1 IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2112, N'Quiz 1 - Stacks and Queues', N'QUIZ', 10.0, 100.0);
    SET @e_quiz1 = SCOPE_IDENTITY();
END

SELECT @e_ca1 = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2112 AND type = 'ASSESSMENT';
IF @e_ca1 IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2112, N'Continuous Assessment 1 - Tree Traversal Project', N'ASSESSMENT', 15.0, 100.0);
    SET @e_ca1 = SCOPE_IDENTITY();
END

SELECT @e_mid = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2112 AND type = 'MID_TERM';
IF @e_mid IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2112, N'Mid-Semester Theory Examination', N'MID_TERM', 25.0, 100.0);
    SET @e_mid = SCOPE_IDENTITY();
END

SELECT @e_prac = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2112 AND type = 'PRACTICAL';
IF @e_prac IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2112, N'Practical Lab Test - Graph Algorithms', N'PRACTICAL', 20.0, 100.0);
    SET @e_prac = SCOPE_IDENTITY();
END

SELECT @e_final = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2112 AND type = 'FINAL';
IF @e_final IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2112, N'End-Semester Final Examination', N'FINAL', 30.0, 100.0);
    SET @e_final = SCOPE_IDENTITY();
END

-- ICT2123 Evaluations
DECLARE @e_db_quiz INT, @e_db_proj INT, @e_db_final INT;
SELECT @e_db_quiz = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2123 AND type = 'QUIZ';
IF @e_db_quiz IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2123, N'Quiz 1 - SQL DDL and DML Queries', N'QUIZ', 10.0, 100.0);
    SET @e_db_quiz = SCOPE_IDENTITY();
END
SELECT @e_db_proj = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2123 AND type = 'ASSESSMENT';
IF @e_db_proj IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2123, N'Database Mini Project Milestone', N'ASSESSMENT', 20.0, 100.0);
    SET @e_db_proj = SCOPE_IDENTITY();
END
SELECT @e_db_final = evaluation_id FROM dbo.evaluations WHERE course_id = @c_ict2123 AND type = 'FINAL';
IF @e_db_final IS NULL
BEGIN
    INSERT INTO dbo.evaluations (course_id, title, type, weightage, max_marks)
    VALUES (@c_ict2123, N'Final Database Written Exam', N'FINAL', 50.0, 100.0);
    SET @e_db_final = SCOPE_IDENTITY();
END

-- ==============================================================================
-- PART B: DATA FOR RASHKAN (UNDERGRADUATE STUDENT)
-- ==============================================================================
PRINT '>>> Feeding Undergraduate Rashkan Data...';

-- B.1 Enrollments for Rashkan
IF NOT EXISTS (SELECT 1 FROM dbo.enrollments WHERE student_id = @rashkan_id AND course_id = @c_ict2112)
    INSERT INTO dbo.enrollments (student_id, course_id, semester_number, academic_year, status)
    VALUES (@rashkan_id, @c_ict2112, 1, N'2025/2026', N'ONGOING');

IF NOT EXISTS (SELECT 1 FROM dbo.enrollments WHERE student_id = @rashkan_id AND course_id = @c_ict2123)
    INSERT INTO dbo.enrollments (student_id, course_id, semester_number, academic_year, status)
    VALUES (@rashkan_id, @c_ict2123, 1, N'2025/2026', N'ONGOING');

IF NOT EXISTS (SELECT 1 FROM dbo.enrollments WHERE student_id = @rashkan_id AND course_id = @c_ict2132)
    INSERT INTO dbo.enrollments (student_id, course_id, semester_number, academic_year, status)
    VALUES (@rashkan_id, @c_ict2132, 2, N'2025/2026', N'ONGOING');

-- B.2 Marks for Rashkan (Both Evaluation Marks & Component Marks)
-- Evaluation Marks for ICT2112 (out of 100)
-- 1. Quiz 1: 88.0
IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_quiz1)
    UPDATE dbo.marks SET marks_obtained = 88.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_quiz1;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2112, @e_quiz1, 88.0);

-- 2. CA 1: 85.0
IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_ca1)
    UPDATE dbo.marks SET marks_obtained = 85.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_ca1;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2112, @e_ca1, 85.0);

-- 3. Mid-Term: 82.0
IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_mid)
    UPDATE dbo.marks SET marks_obtained = 82.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_mid;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2112, @e_mid, 82.0);

-- 4. Practical: 90.0
IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_prac)
    UPDATE dbo.marks SET marks_obtained = 90.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_prac;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2112, @e_prac, 90.0);

-- 5. Final Exam: 86.0
IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_final)
    UPDATE dbo.marks SET marks_obtained = 86.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_final;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2112, @e_final, 86.0);

-- Evaluation Marks for ICT2123
IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_db_quiz)
    UPDATE dbo.marks SET marks_obtained = 82.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_db_quiz;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2123, @e_db_quiz, 82.0);

IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_db_proj)
    UPDATE dbo.marks SET marks_obtained = 86.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_db_proj;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2123, @e_db_proj, 86.0);

IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND evaluation_id = @e_db_final)
    UPDATE dbo.marks SET marks_obtained = 84.0 WHERE student_id = @rashkan_id AND evaluation_id = @e_db_final;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, marks_obtained)
    VALUES (@rashkan_id, @c_ict2123, @e_db_final, 84.0);

-- Component Marks for Course Level (Member 4 summary view)
IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND course_id = @c_ict2112 AND evaluation_id IS NULL)
    UPDATE dbo.marks SET quiz_marks = 88.0, assignment_marks = 85.0, mid_sem_marks = 82.0, practical_marks = 90.0, final_exam_marks = 86.0
    WHERE student_id = @rashkan_id AND course_id = @c_ict2112 AND evaluation_id IS NULL;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, quiz_marks, assignment_marks, mid_sem_marks, practical_marks, final_exam_marks)
    VALUES (@rashkan_id, @c_ict2112, NULL, 88.0, 85.0, 82.0, 90.0, 86.0);

IF EXISTS (SELECT 1 FROM dbo.marks WHERE student_id = @rashkan_id AND course_id = @c_ict2123 AND evaluation_id IS NULL)
    UPDATE dbo.marks SET quiz_marks = 82.0, assignment_marks = 86.0, mid_sem_marks = 80.0, practical_marks = 85.0, final_exam_marks = 84.0
    WHERE student_id = @rashkan_id AND course_id = @c_ict2123 AND evaluation_id IS NULL;
ELSE
    INSERT INTO dbo.marks (student_id, course_id, evaluation_id, quiz_marks, assignment_marks, mid_sem_marks, practical_marks, final_exam_marks)
    VALUES (@rashkan_id, @c_ict2123, NULL, 82.0, 86.0, 80.0, 85.0, 84.0);

-- B.3 CA Eligibility for Rashkan
-- ICT2112: CA = (88*0.10 + 85*0.15 + 82*0.25 + 90*0.20) / 0.70 = 85.78% -> ELIGIBLE
IF EXISTS (SELECT 1 FROM dbo.eligibility WHERE student_id = @rashkan_id AND course_id = @c_ict2112)
    UPDATE dbo.eligibility SET ca_percentage = 85.78, is_eligible = 1 WHERE student_id = @rashkan_id AND course_id = @c_ict2112;
ELSE
    INSERT INTO dbo.eligibility (student_id, course_id, ca_percentage, is_eligible)
    VALUES (@rashkan_id, @c_ict2112, 85.78, 1);

-- ICT2123: CA = 83.50% -> ELIGIBLE
IF EXISTS (SELECT 1 FROM dbo.eligibility WHERE student_id = @rashkan_id AND course_id = @c_ict2123)
    UPDATE dbo.eligibility SET ca_percentage = 83.50, is_eligible = 1 WHERE student_id = @rashkan_id AND course_id = @c_ict2123;
ELSE
    INSERT INTO dbo.eligibility (student_id, course_id, ca_percentage, is_eligible)
    VALUES (@rashkan_id, @c_ict2123, 83.50, 1);

-- B.4 Academic Grades for Rashkan
IF EXISTS (SELECT 1 FROM dbo.grades WHERE student_id = @rashkan_id AND course_id = @c_ict2112)
    UPDATE dbo.grades SET ca_marks = 85.78, final_exam_marks = 86.00, final_total_marks = 85.85,
                          ca_eligible = 1, grade_letter = N'A', grade_point = 4.00
    WHERE student_id = @rashkan_id AND course_id = @c_ict2112;
ELSE
    INSERT INTO dbo.grades (student_id, course_id, semester_number, academic_year, ca_marks, final_exam_marks, final_total_marks, ca_eligible, grade_letter, grade_point)
    VALUES (@rashkan_id, @c_ict2112, 1, N'2025/2026', 85.78, 86.00, 85.85, 1, N'A', 4.00);

IF EXISTS (SELECT 1 FROM dbo.grades WHERE student_id = @rashkan_id AND course_id = @c_ict2123)
    UPDATE dbo.grades SET ca_marks = 83.50, final_exam_marks = 84.00, final_total_marks = 83.75,
                          ca_eligible = 1, grade_letter = N'A-', grade_point = 3.70
    WHERE student_id = @rashkan_id AND course_id = @c_ict2123;
ELSE
    INSERT INTO dbo.grades (student_id, course_id, semester_number, academic_year, ca_marks, final_exam_marks, final_total_marks, ca_eligible, grade_letter, grade_point)
    VALUES (@rashkan_id, @c_ict2123, 1, N'2025/2026', 83.50, 84.00, 83.75, 1, N'A-', 3.70);

-- B.5 Semester Results for Rashkan
IF EXISTS (SELECT 1 FROM dbo.results WHERE student_id = @rashkan_id AND semester_number = 1)
    UPDATE dbo.results SET sgpa = 3.850, credits_attempted = 15, credits_completed = 15
    WHERE student_id = @rashkan_id AND semester_number = 1;
ELSE
    INSERT INTO dbo.results (student_id, semester_number, academic_year, sgpa, credits_attempted, credits_completed)
    VALUES (@rashkan_id, 1, N'2025/2026', 3.850, 15, 15);

IF EXISTS (SELECT 1 FROM dbo.results WHERE student_id = @rashkan_id AND semester_number = 2)
    UPDATE dbo.results SET sgpa = 3.900, credits_attempted = 15, credits_completed = 15
    WHERE student_id = @rashkan_id AND semester_number = 2;
ELSE
    INSERT INTO dbo.results (student_id, semester_number, academic_year, sgpa, credits_attempted, credits_completed)
    VALUES (@rashkan_id, 2, N'2025/2026', 3.900, 15, 15);

-- B.6 Medical Records for Rashkan
IF NOT EXISTS (SELECT 1 FROM dbo.medical_records WHERE student_id = @rashkan_id AND reason LIKE N'%High fever%')
    INSERT INTO dbo.medical_records (student_id, course_id, session_id, medical_date, reason, status, document_path)
    VALUES (@rashkan_id, N'ICT2112', NULL, CAST('2026-06-15' AS DATE), N'High fever and viral influenza treated at Karapitiya Teaching Hospital', N'APPROVED', N'resources/medicals/rashkan_med01.pdf');

IF NOT EXISTS (SELECT 1 FROM dbo.medical_records WHERE student_id = @rashkan_id AND reason LIKE N'%Dental surgery%')
    INSERT INTO dbo.medical_records (student_id, course_id, session_id, medical_date, reason, status, document_path)
    VALUES (@rashkan_id, N'ICT2123', NULL, CAST('2026-06-25' AS DATE), N'Emergency dental surgery and clinical observation at University Health Center', N'PENDING', N'resources/medicals/rashkan_med02.pdf');

-- Medical Submissions for Rashkan
IF NOT EXISTS (SELECT 1 FROM dbo.medical_submissions WHERE student_id = @rashkan_id AND reason LIKE N'%High fever%')
    INSERT INTO dbo.medical_submissions (student_id, course_id, start_date, end_date, reason, document_path, status)
    VALUES (@rashkan_id, @c_ict2112, CAST('2026-06-14' AS DATE), CAST('2026-06-16' AS DATE), N'High fever and viral influenza treated at Karapitiya Teaching Hospital', N'resources/medicals/rashkan_med01.pdf', N'APPROVED');

IF NOT EXISTS (SELECT 1 FROM dbo.medical_submissions WHERE student_id = @rashkan_id AND reason LIKE N'%Dental surgery%')
    INSERT INTO dbo.medical_submissions (student_id, course_id, start_date, end_date, reason, document_path, status)
    VALUES (@rashkan_id, @c_ict2123, CAST('2026-06-24' AS DATE), CAST('2026-06-26' AS DATE), N'Emergency dental surgery at University Health Center', N'resources/medicals/rashkan_med02.pdf', N'PENDING');

-- ==============================================================================
-- PART C: DATA FOR HANSIKA (TECHNICAL OFFICER)
-- ==============================================================================
PRINT '>>> Feeding Technical Officer Hansika Data...';

-- C.1 Attendance Sessions Created by Hansika
-- Ensure Hansika has created both Theory & Practical Sessions
DECLARE @s_h1 INT, @s_h2 INT, @s_h3 INT;

IF NOT EXISTS (SELECT 1 FROM dbo.attendance_sessions WHERE course_id = 'ICT2112' AND session_number = 1 AND session_type = 'THEORY' AND created_by = @hansika_id)
BEGIN
    INSERT INTO dbo.attendance_sessions (course_id, session_type, session_number, session_date, hours, created_by)
    VALUES (N'ICT2112', N'THEORY', 1, CAST('2026-06-08' AS DATE), 2.0, @hansika_id);
    SET @s_h1 = SCOPE_IDENTITY();
END
ELSE
    SELECT @s_h1 = session_id FROM dbo.attendance_sessions WHERE course_id = 'ICT2112' AND session_number = 1 AND session_type = 'THEORY' AND created_by = @hansika_id;

IF NOT EXISTS (SELECT 1 FROM dbo.attendance_sessions WHERE course_id = 'ICT2112' AND session_number = 2 AND session_type = 'THEORY' AND created_by = @hansika_id)
BEGIN
    INSERT INTO dbo.attendance_sessions (course_id, session_type, session_number, session_date, hours, created_by)
    VALUES (N'ICT2112', N'THEORY', 2, CAST('2026-06-15' AS DATE), 2.0, @hansika_id);
    SET @s_h2 = SCOPE_IDENTITY();
END
ELSE
    SELECT @s_h2 = session_id FROM dbo.attendance_sessions WHERE course_id = 'ICT2112' AND session_number = 2 AND session_type = 'THEORY' AND created_by = @hansika_id;

IF NOT EXISTS (SELECT 1 FROM dbo.attendance_sessions WHERE course_id = 'ICT2112' AND session_number = 1 AND session_type = 'PRACTICAL' AND created_by = @hansika_id)
BEGIN
    INSERT INTO dbo.attendance_sessions (course_id, session_type, session_number, session_date, hours, created_by)
    VALUES (N'ICT2112', N'PRACTICAL', 1, CAST('2026-06-10' AS DATE), 2.0, @hansika_id);
    SET @s_h3 = SCOPE_IDENTITY();
END
ELSE
    SELECT @s_h3 = session_id FROM dbo.attendance_sessions WHERE course_id = 'ICT2112' AND session_number = 1 AND session_type = 'PRACTICAL' AND created_by = @hansika_id;

-- C.2 Mark Attendance for Rashkan in Hansika's Sessions
IF @s_h1 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.attendance_records WHERE session_id = @s_h1 AND student_id = @rashkan_id)
    INSERT INTO dbo.attendance_records (session_id, student_id, course_id, status, theory_attended, theory_total, practical_attended, practical_total)
    VALUES (@s_h1, @rashkan_id, @c_ict2112, N'PRESENT', 14, 15, 14, 15);

IF @s_h2 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.attendance_records WHERE session_id = @s_h2 AND student_id = @rashkan_id)
    INSERT INTO dbo.attendance_records (session_id, student_id, course_id, status, theory_attended, theory_total, practical_attended, practical_total)
    VALUES (@s_h2, @rashkan_id, @c_ict2112, N'PRESENT', 14, 15, 14, 15);

IF @s_h3 IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.attendance_records WHERE session_id = @s_h3 AND student_id = @rashkan_id)
    INSERT INTO dbo.attendance_records (session_id, student_id, course_id, status, theory_attended, theory_total, practical_attended, practical_total)
    VALUES (@s_h3, @rashkan_id, @c_ict2112, N'PRESENT', 14, 15, 14, 15);

-- Ensure course summary attendance record for Rashkan
IF EXISTS (SELECT 1 FROM dbo.attendance_records WHERE student_id = @rashkan_id AND course_id = @c_ict2112 AND session_id IS NULL)
    UPDATE dbo.attendance_records SET status = N'PRESENT', theory_attended = 14, theory_total = 15, practical_attended = 14, practical_total = 15
    WHERE student_id = @rashkan_id AND course_id = @c_ict2112 AND session_id IS NULL;
ELSE
    INSERT INTO dbo.attendance_records (session_id, student_id, course_id, status, theory_attended, theory_total, practical_attended, practical_total)
    VALUES (NULL, @rashkan_id, @c_ict2112, N'PRESENT', 14, 15, 14, 15);

PRINT '==============================================================================';
PRINT '>>> Successfully fed full role data for Aysha, Rashkan, and Hansika!';
PRINT '==============================================================================';
GO

-- Verification query
SELECT u.user_id, u.username, u.password, r.role_name, u.first_name + ' ' + u.last_name AS full_name, u.email, u.status
FROM dbo.users u
JOIN dbo.roles r ON u.role_id = r.role_id
WHERE UPPER(u.username) IN ('AYSHA', 'RASHKAN', 'HANSIKA');
GO
