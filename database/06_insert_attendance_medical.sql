-- ================================================================================
-- Seed Script: Member 3 — Attendance Sessions, Attendance Records & Medicals
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

USE faculty_management_db;

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
