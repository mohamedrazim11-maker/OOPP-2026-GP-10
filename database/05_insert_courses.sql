-- =======================================================
-- Seed Data Script: Courses
-- Faculty Management System (Faculty of Technology)
-- =======================================================

USE faculty_management_db;

INSERT INTO courses (course_code, course_name, credit_value, theory_hours, practical_hours, department, semester, description) VALUES
('ICT2112', 'Data Structures and Algorithms', 2, 30, 30, 'Department of Information & Communication Technology', 'Semester 2', 'Core algorithms, abstract data types, trees, graphs, and complexity analysis'),
('ICT2123', 'Database Management Systems', 3, 30, 60, 'Department of Information & Communication Technology', 'Semester 2', 'Relational data modeling, SQL programming, normalization, and indexing'),
('ICT2132', 'Object Oriented Programming Practicum', 2, 0, 60, 'Department of Information & Communication Technology', 'Semester 2', 'Comprehensive hands-on OOP software development using Java Swing and MySQL'),
('BST2113', 'Bioprocess Technology', 3, 30, 60, 'Department of Biosystems Technology', 'Semester 2', 'Bioreactor engineering, downstream processing, and industrial applications'),
('ENG2113', 'Engineering Mechanics', 3, 30, 60, 'Department of Engineering Technology', 'Semester 2', 'Engineering statics, dynamics, stress analysis, and structural mechanics')
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);
