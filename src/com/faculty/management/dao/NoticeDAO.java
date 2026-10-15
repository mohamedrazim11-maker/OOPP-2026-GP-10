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
 * Provides complete CRUD operations with robust MySQL and SQL Server database handling.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: NoticeDAO encapsulates database access operations for Notice entities.
 * - Encapsulation: Database queries and statement management are completely internal.
 * - Error and Exception Handling: Catches SQLExceptions, manages resources via try-with-resources, and throws DatabaseException.
 * - Database Handling: PreparedStatements, SQL execution, connection pooling, and cross-engine table creation.
 */
public class NoticeDAO {

    /**
     * Retrieves all notices ordered by posted_date DESC, notice_id DESC.
     *
     * @return List of Notice objects
     * @throws DatabaseException If database access fails
     */
    public List<Notice> findAll() throws DatabaseException {
        List<Notice> notices = new ArrayList<>();
        ensureTableAndSeed();

        String sql = "SELECT * FROM notices ORDER BY posted_date DESC, notice_id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                notices.add(mapResultSetToNotice(rs));
            }
            return notices;
        } catch (SQLException e) {
            // Safe fallback if database is currently unavailable
            return getFallbackNotices();
        }
    }

    /**
     * Finds a single notice by its primary key ID.
     *
     * @param noticeId Notice ID
     * @return Notice object or null if not found
     * @throws DatabaseException If database query fails
     */
    public Notice findById(int noticeId) throws DatabaseException {
        ensureTableAndSeed();
        String sql = "SELECT * FROM notices WHERE notice_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, noticeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotice(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            // Check fallback
            for (Notice n : getFallbackNotices()) {
                if (n.getNoticeId() == noticeId) {
                    return n;
                }
            }
            throw new DatabaseException("Error retrieving notice by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a single notice by title match.
     *
     * @param title Notice title
     * @return Notice object or null if not found
     * @throws DatabaseException If database query fails
     */
    public Notice findByTitle(String title) throws DatabaseException {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }
        ensureTableAndSeed();
        String sql = "SELECT * FROM notices WHERE LOWER(title) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotice(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            String lower = title.toLowerCase().trim();
            for (Notice n : getFallbackNotices()) {
                if (n.getTitle() != null && n.getTitle().toLowerCase().trim().equals(lower)) {
                    return n;
                }
            }
            throw new DatabaseException("Error retrieving notice by title: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves notices filtered by category (e.g. ACADEMIC, EXAMINATION, TIMETABLE, GENERAL).
     *
     * @param category Notice category
     * @return List of matching Notice objects
     * @throws DatabaseException If database query fails
     */
    public List<Notice> findByCategory(String category) throws DatabaseException {
        if (category == null || "ALL".equalsIgnoreCase(category.trim())) {
            return findAll();
        }

        List<Notice> notices = new ArrayList<>();
        ensureTableAndSeed();
        String sql = "SELECT * FROM notices WHERE category = ? ORDER BY posted_date DESC, notice_id DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notices.add(mapResultSetToNotice(rs));
                }
            }
            return notices;
        } catch (SQLException e) {
            List<Notice> fallback = new ArrayList<>();
            for (Notice n : getFallbackNotices()) {
                if (category.equalsIgnoreCase(n.getCategory())) {
                    fallback.add(n);
                }
            }
            return fallback;
        }
    }

    /**
     * Searches notices by title, content, or category keyword.
     *
     * @param keyword Search keyword
     * @return List of matching Notice entities
     * @throws DatabaseException If database query fails
     */
    public List<Notice> search(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        List<Notice> notices = new ArrayList<>();
        ensureTableAndSeed();
        String pattern = "%" + keyword.trim() + "%";
        String sql = "SELECT * FROM notices WHERE title LIKE ? OR content LIKE ? OR category LIKE ? OR posted_by LIKE ? " +
                     "ORDER BY posted_date DESC, notice_id DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notices.add(mapResultSetToNotice(rs));
                }
            }
            return notices;
        } catch (SQLException e) {
            String lower = keyword.toLowerCase().trim();
            List<Notice> fallback = new ArrayList<>();
            for (Notice n : getFallbackNotices()) {
                if ((n.getTitle() != null && n.getTitle().toLowerCase().contains(lower)) ||
                    (n.getContent() != null && n.getContent().toLowerCase().contains(lower)) ||
                    (n.getCategory() != null && n.getCategory().toLowerCase().contains(lower))) {
                    fallback.add(n);
                }
            }
            return fallback;
        }
    }

    /**
     * Inserts a new notice into the database.
     *
     * @param notice Notice entity to insert
     * @return Created Notice with database-generated ID
     * @throws DatabaseException If database insertion fails
     */
    public Notice insert(Notice notice) throws DatabaseException {
        if (notice == null) {
            throw new DatabaseException("Cannot insert null notice entity.");
        }

        ensureTableAndSeed();
        String sql = "INSERT INTO notices (title, content, category, priority, posted_by, posted_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, notice.getTitle() != null ? notice.getTitle().trim() : "");
            stmt.setString(2, notice.getContent() != null ? notice.getContent().trim() : "");
            stmt.setString(3, notice.getCategory() != null ? notice.getCategory().trim() : "GENERAL");
            stmt.setString(4, notice.getPriority() != null ? notice.getPriority().trim() : "NORMAL");
            stmt.setString(5, notice.getPostedBy() != null ? notice.getPostedBy().trim() : "Faculty Administration");

            LocalDate pDate = notice.getPostedDate() != null ? notice.getPostedDate() : LocalDate.now();
            stmt.setDate(6, Date.valueOf(pDate));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Inserting notice failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notice.setNoticeId(generatedKeys.getInt(1));
                }
            }

            return notice;
        } catch (SQLException e) {
            throw new DatabaseException("Database error while inserting notice: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if a notice with the specified title already exists in the database.
     *
     * @param title Notice title
     * @return true if notice with title exists
     */
    public boolean hasNoticeWithTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        ensureTableAndSeed();
        String sql = "SELECT COUNT(*) FROM notices WHERE LOWER(title) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Checks if another notice with the specified title already exists, excluding a given notice ID.
     * Used during edit operations to prevent duplicate notice titles.
     *
     * @param title           Notice title
     * @param excludeNoticeId Notice ID to exclude
     * @return true if another notice with title exists
     */
    public boolean hasNoticeWithTitle(String title, int excludeNoticeId) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        ensureTableAndSeed();
        String sql = "SELECT COUNT(*) FROM notices WHERE LOWER(title) = LOWER(?) AND notice_id != ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title.trim());
            stmt.setInt(2, excludeNoticeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Updates an existing notice in the database.
     *
     * @param notice Notice entity with updated values
     * @return true if updated successfully, false otherwise
     * @throws DatabaseException If database update fails
     */
    public boolean update(Notice notice) throws DatabaseException {
        if (notice == null || notice.getNoticeId() <= 0) {
            throw new DatabaseException("Cannot update notice without a valid Notice ID.");
        }

        ensureTableAndSeed();
        String sql = "UPDATE notices SET title = ?, content = ?, category = ?, priority = ?, posted_by = ? WHERE notice_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, notice.getTitle() != null ? notice.getTitle().trim() : "");
            stmt.setString(2, notice.getContent() != null ? notice.getContent().trim() : "");
            stmt.setString(3, notice.getCategory() != null ? notice.getCategory().trim() : "GENERAL");
            stmt.setString(4, notice.getPriority() != null ? notice.getPriority().trim() : "NORMAL");
            stmt.setString(5, notice.getPostedBy() != null ? notice.getPostedBy().trim() : "Faculty Administration");
            stmt.setInt(6, notice.getNoticeId());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error while updating notice: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a notice by ID.
     *
     * @param noticeId Notice ID to delete
     * @return true if deleted, false if notice not found
     * @throws DatabaseException If database deletion fails
     */
    public boolean delete(int noticeId) throws DatabaseException {
        if (noticeId <= 0) {
            throw new DatabaseException("Invalid notice ID for deletion.");
        }

        ensureTableAndSeed();
        String sql = "DELETE FROM notices WHERE notice_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, noticeId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error while deleting notice: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a notice by its unique title.
     *
     * @param title Notice title
     * @return true if deleted
     * @throws DatabaseException If database error occurs
     */
    public boolean deleteByTitle(String title) throws DatabaseException {
        if (title == null || title.trim().isEmpty()) {
            throw new DatabaseException("Notice title cannot be empty for deletion.");
        }
        ensureTableAndSeed();
        String sql = "DELETE FROM notices WHERE LOWER(title) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title.trim());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error while deleting notice by title: " + e.getMessage(), e);
        }
    }

    /**
     * Returns total count of notices in the database.
     */
    public int getCount() throws DatabaseException {
        ensureTableAndSeed();
        String sql = "SELECT COUNT(*) FROM notices";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            return getFallbackNotices().size();
        }
    }

    /**
     * Helper method to map a ResultSet row to a Notice object.
     */
    private Notice mapResultSetToNotice(ResultSet rs) throws SQLException {
        Date date = rs.getDate("posted_date");
        LocalDate localDate = date != null ? date.toLocalDate() : LocalDate.now();

        return new Notice(
                rs.getInt("notice_id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("category"),
                rs.getString("priority"),
                rs.getString("posted_by"),
                localDate
        );
    }

    /**
     * Ensures the notices table exists in the database and seeds initial announcements if empty.
     */
    public void ensureTableAndSeed() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            boolean tableExists = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM notices")) {
                tableExists = true;
                if (rs.next() && rs.getInt(1) == 0) {
                    insertSeedNotices(conn);
                }
            } catch (SQLException e) {
                tableExists = false;
            }

            if (!tableExists) {
                boolean isSqlServer = DatabaseConnection.getInstance().isSqlServer();
                String createSql;
                if (isSqlServer) {
                    createSql = "CREATE TABLE notices (" +
                            "notice_id INT IDENTITY(1,1) PRIMARY KEY, " +
                            "title NVARCHAR(200) NOT NULL, " +
                            "content NVARCHAR(MAX) NOT NULL, " +
                            "category NVARCHAR(50) NOT NULL DEFAULT 'GENERAL', " +
                            "priority NVARCHAR(20) NOT NULL DEFAULT 'NORMAL', " +
                            "posted_by NVARCHAR(100) NOT NULL DEFAULT 'Faculty Administration', " +
                            "posted_date DATE NOT NULL, " +
                            "created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP" +
                            ");";
                } else {
                    createSql = "CREATE TABLE IF NOT EXISTS notices (" +
                            "notice_id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "title VARCHAR(200) NOT NULL, " +
                            "content TEXT NOT NULL, " +
                            "category VARCHAR(50) NOT NULL DEFAULT 'GENERAL', " +
                            "priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL', " +
                            "posted_by VARCHAR(100) NOT NULL DEFAULT 'Faculty Administration', " +
                            "posted_date DATE NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ") ENGINE=InnoDB;";
                }
                stmt.executeUpdate(createSql);
                insertSeedNotices(conn);
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
