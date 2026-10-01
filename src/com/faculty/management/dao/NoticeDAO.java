package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Notice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for official faculty notices and circulars.
 * Ensures the `notices` table exists and provides seed announcements.
 */
public class NoticeDAO {

    public List<Notice> findAll() throws DatabaseException {
        List<Notice> notices = new ArrayList<>();
        try {
            ensureTableAndSeed();
            String sql = "SELECT * FROM notices ORDER BY posted_date DESC, notice_id DESC";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Date date = rs.getDate("posted_date");
                    LocalDate localDate = date != null ? date.toLocalDate() : LocalDate.now();

                    notices.add(new Notice(
                            rs.getInt("notice_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("category"),
                            rs.getString("priority"),
                            rs.getString("posted_by"),
                            localDate
                    ));
                }
            }
        } catch (Exception e) {
            // Provide in-memory fallback list if database is offline or encountering errors
            return getFallbackNotices();
        }

        if (notices.isEmpty()) {
            return getFallbackNotices();
        }
        return notices;
    }

    private void ensureTableAndSeed() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            String createSql = "CREATE TABLE IF NOT EXISTS notices (" +
                    "notice_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "category VARCHAR(50) NOT NULL DEFAULT 'GENERAL', " +
                    "priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL', " +
                    "posted_by VARCHAR(100) NOT NULL DEFAULT 'Faculty Administration', " +
                    "posted_date DATE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB;";
            stmt.executeUpdate(createSql);

            // Check if rows exist
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM notices")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    insertSeedNotices(conn);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void insertSeedNotices(Connection conn) {
        String insertSql = "INSERT INTO notices (title, content, category, priority, posted_by, posted_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (Notice n : getFallbackNotices()) {
                stmt.setString(1, n.getTitle());
                stmt.setString(2, n.getContent());
                stmt.setString(3, n.getCategory());
                stmt.setString(4, n.getPriority());
                stmt.setString(5, n.getPostedBy());
                stmt.setDate(6, Date.valueOf(n.getPostedDate()));
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException ignored) {
        }
    }

    public static List<Notice> getFallbackNotices() {
        List<Notice> list = new ArrayList<>();
        LocalDate today = LocalDate.now();

        list.add(new Notice(
                1,
                "Mandatory 80% Attendance Requirement for Semester 2 Examinations",
                "All undergraduates of the Faculty of Technology are hereby reminded that an overall attendance of at least 80% in both theory lectures and laboratory sessions is mandatory to be deemed eligible to sit for the upcoming semester final examinations. Students falling below 80% must immediately verify that all medical certificates have been formally lodged with the Technical Officer.",
                "ACADEMIC",
                "URGENT",
                "Department of Information and Communication Technology",
                today.minusDays(1)
        ));

        list.add(new Notice(
                2,
                "Continuous Assessment (CA >= 40%) Eligibility Lists Published",
                "The provisional Continuous Assessment (CA) scores and eligibility statuses for Level II Semester I modules (CS201, CS202, CS203, CS204, MA201) have been compiled. In accordance with university curriculum criteria, students must achieve a minimum CA score of 40% out of 100 to sit for the respective end-semester exam. Please check your individual grade transcript.",
                "EXAMINATION",
                "URGENT",
                "Examination Division & Course Coordinators",
                today.minusDays(3)
        ));

        list.add(new Notice(
                3,
                "Laboratory Session Venue Adjustment — Computer Lab 1 & 2",
                "Commencing Monday next week, practical laboratory sessions for Object Oriented Programming (CS202) and Database Management Systems (CS203) will be conducted in Computer Lab 2 (Level 2, Technology Complex). Please refer to the updated class timetable on your student dashboard.",
                "TIMETABLE",
                "NORMAL",
                "Technical Officer in Charge",
                today.minusDays(5)
        ));

        list.add(new Notice(
                4,
                "Submission Deadline for Outstanding Medical Certificates",
                "Undergraduates with excused absences between Week 1 and Week 10 must lodge certified medical certificates within 7 days of this announcement. Certificates must be issued by a registered Government Medical Officer (GMO) and uploaded as a PDF via the student portal Medical tab.",
                "GENERAL",
                "NORMAL",
                "Senior Assistant Registrar / Academic",
                today.minusDays(7)
        ));

        list.add(new Notice(
                5,
                "UGC Circular No. 12-2024 Grading System Implementation Guidelines",
                "Official notification regarding the standard Sri Lankan University Grants Commission (UGC) Circular No. 12-2024 grading criteria. Letter grades (A+ to E) and corresponding Grade Point Values (4.00 to 0.00) are mapped based on 40% CA + 60% Final Examination weighting. Cumulative CGPA classifications are available on the CGPA breakdown tab.",
                "ACADEMIC",
                "INFO",
                "Dean's Office, Faculty of Technology",
                today.minusDays(10)
        ));

        return list;
    }
}
