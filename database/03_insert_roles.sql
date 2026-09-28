-- =======================================================
-- Seed Script: User Roles
-- Faculty Management System
-- =======================================================

USE faculty_management_db;

INSERT INTO roles (role_id, role_name, description) VALUES
(1, 'ADMIN', 'System Administrator with full management privileges'),
(2, 'LECTURER', 'Academic staff handling courses, marks, materials, and student progress'),
(3, 'TECHNICAL_OFFICER', 'Technical Officer handling attendance, lab sessions, and medical submissions'),
(4, 'UNDERGRADUATE', 'Enrolled student accessing attendance, courses, grades, notices, and timetables')
ON DUPLICATE KEY UPDATE 
    role_name = VALUES(role_name),
    description = VALUES(description);
