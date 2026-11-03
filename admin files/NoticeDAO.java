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
            // Throws exception when database is unavailable
            throw new DatabaseException("Error retrieving notices from database: " + e.getMessage(), e);
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
            throw new DatabaseException("Error retrieving notices by category: " + e.getMessage(), e);
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
            throw new DatabaseException("Error searching notices: " + e.getMessage(), e);
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
            return 0;
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
                    // Table exists
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
                // Table created without seed notices
            }
        } catch (Exception ignored) {
        }
    }

    private void insertSeedNotices(Connection conn) {
        // No-op: mock seed notices removed
    }

    public static List<Notice> getFallbackNotices() {
        return new ArrayList<>();
    }
}
