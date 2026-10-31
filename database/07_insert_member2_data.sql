-- =======================================================
-- Seed Script: Member 2 (Lecturer & Academic Management)
-- Handles: course_materials, course_lecturers, evaluations, marks, eligibility
-- Faculty Management System
-- =======================================================

USE faculty_management_db;

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
