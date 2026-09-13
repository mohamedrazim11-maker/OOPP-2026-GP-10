-- =======================================================
-- Seed Script: User Accounts
-- Faculty Management System
-- =======================================================

USE faculty_management_db;

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
