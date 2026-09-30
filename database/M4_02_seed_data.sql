-- =======================================================
-- Seed Script: Member 4 — Courses, Enrollments, Marks, Timetable
-- Faculty Management System | ICT2132 Mini Project
-- Demo dataset: 20 undergraduates across 2 semesters (10 courses)
-- =======================================================

USE faculty_management_db;

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
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name), credits = VALUES(credits);

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
-- from an earlier failed attempt — already covered by the bulk enrollment
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
-- Timetable (current semester — Semester 2, AY 2025/2026)
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
       CASE WHEN c.practical_sessions > 0 THEN GREATEST(0, c.practical_sessions - 1) ELSE 0 END, c.practical_sessions
FROM enrollments e JOIN courses c ON c.course_id = e.course_id
ON DUPLICATE KEY UPDATE theory_attended = VALUES(theory_attended), theory_total = VALUES(theory_total),
                        practical_attended = VALUES(practical_attended), practical_total = VALUES(practical_total);
