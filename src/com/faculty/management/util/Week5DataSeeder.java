package com.faculty.management.util;

import com.faculty.management.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Utility Class to seed sample data (20+ Students, Courses, Evaluations, Marks, Eligibility)
 * aligned with the strict SQL table schema.
 */
public class Week5DataSeeder {

    public static void main(String[] args) {
        System.out.println("=== Starting Week 5 Sample Data Seeding ===");

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Seed Roles
            stmt.executeUpdate("INSERT IGNORE INTO roles (role_id, role_name, description) VALUES " +
                    "(1, 'ADMIN', 'System Administrator'), " +
                    "(2, 'LECTURER', 'Academic Staff'), " +
                    "(3, 'STUDENT', 'Undergraduate Student');");

            // 2. Seed Sample Lecturer User
            stmt.executeUpdate("INSERT IGNORE INTO users (user_id, username, password, email, first_name, last_name, role_id, status) VALUES " +
                    "(100, 'lecturer1', 'pass123', 'lecturer1@fot.sjp.ac.lk', 'Dr. John', 'Doe', 2, 'ACTIVE');");

            // 3. Seed 20 Students into Users Table (role_id = 3)
            String userSql = "INSERT IGNORE INTO users (user_id, username, password, email, first_name, last_name, role_id, status) VALUES (?, ?, 'pass123', ?, ?, 'Student', 3, 'ACTIVE')";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                for (int i = 1; i <= 20; i++) {
                    int studentUserId = 1000 + i;
                    userStmt.setInt(1, studentUserId);
                    userStmt.setString(2, "tg" + (100 + i));
                    userStmt.setString(3, "tg" + (100 + i) + "@fot.sjp.ac.lk");
                    userStmt.setString(4, "Student_" + i);
                    userStmt.addBatch();
                }
                userStmt.executeBatch();
            }

            // 4. Seed Courses
            stmt.executeUpdate("INSERT IGNORE INTO courses (course_id, course_code, course_name, credit_value, department, semester) VALUES " +
                    "(101, 'ICT1113', 'Object Oriented Programming', 3, 'Information & Communication Technology', 'Semester 2'), " +
                    "(102, 'ICT1123', 'Database Management Systems', 3, 'Information & Communication Technology', 'Semester 2'), " +
                    "(103, 'ICT1134', 'Data Structures & Algorithms', 4, 'Information & Communication Technology', 'Semester 2');");

            // 5. Seed Course Materials (uploaded_by = 100)
            stmt.executeUpdate("INSERT IGNORE INTO course_materials (material_id, course_id, uploaded_by, title, file_path) VALUES " +
                    "(1, 101, 100, 'Lecture 01 - Java Basics', '/uploads/lecture1.pdf'), " +
                    "(2, 101, 100, 'Lecture 02 - OOP Concepts', '/uploads/lecture2.pdf');");

            // 6. Seed Evaluations
            stmt.executeUpdate("INSERT IGNORE INTO evaluations (evaluation_id, course_id, title, type, weightage, max_marks) VALUES " +
                    "(1, 101, 'Quiz 1', 'QUIZ', 10.0, 100.0), " +
                    "(2, 101, 'Midterm Assessment', 'MID_TERM', 30.0, 100.0), " +
                    "(3, 101, 'Final Practical', 'PRACTICAL', 60.0, 100.0);");

            // 7. Seed Marks & CA Eligibility for 20 Students
            String markSql = "INSERT INTO marks (student_id, evaluation_id, marks_obtained) VALUES (?, 1, ?) " +
                    "ON DUPLICATE KEY UPDATE marks_obtained = VALUES(marks_obtained)";

            // SQL query explicitly contains 4 parameter placeholders (?)
            String eligSql = "INSERT INTO eligibility (student_id, course_id, ca_percentage, is_eligible) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE ca_percentage = VALUES(ca_percentage), is_eligible = VALUES(is_eligible)";

            try (PreparedStatement markStmt = conn.prepareStatement(markSql);
                 PreparedStatement eligStmt = conn.prepareStatement(eligSql)) {

                for (int i = 1; i <= 20; i++) {
                    int studentUserId = 1000 + i;
                    double score = 10.0 + (i * 4.0); // Scores range between 14.0 and 90.0
                    if (score > 100.0) score = 95.0;

                    // Insert Mark (2 parameters: student_id, marks_obtained)
                    markStmt.setInt(1, studentUserId);
                    markStmt.setDouble(2, score);
                    markStmt.addBatch();

                    // Calculate CA Eligibility (4 parameters: student_id, course_id, ca_percentage, is_eligible)
                    double caPct = score; // Out of 100.0
                    boolean isEligible = caPct >= 40.0;

                    eligStmt.setInt(1, studentUserId);
                    eligStmt.setInt(2, 101); // course_id
                    eligStmt.setDouble(3, caPct);
                    eligStmt.setBoolean(4, isEligible);
                    eligStmt.addBatch();
                }
                markStmt.executeBatch();
                eligStmt.executeBatch();
            }

            System.out.println("✅ Sample dataset successfully loaded!");
            System.out.println(" - 3 Roles & 1 Lecturer Account inserted");
            System.out.println(" - 20 Student User Accounts created (user_id: 1001-1020)");
            System.out.println(" - 3 Courses & 2 Materials inserted");
            System.out.println(" - 20 Student Marks & Eligibility Records populated");

        } catch (Exception e) {
            System.err.println("❌ Seeding Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}