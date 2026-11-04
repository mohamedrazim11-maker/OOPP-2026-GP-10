package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Batch;
import com.faculty.management.model.Role;
import com.faculty.management.model.Undergraduate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for undergraduate batches.
 * Handles database operations for batch entities and ensures database schema consistency.
 *
 * Demonstrates:
 * - Database Handling: Direct JDBC execution against Microsoft SQL Server and MySQL.
 * - Error & Exception Handling: Catches SQLExceptions and throws informative DatabaseExceptions.
 * - Encapsulation: Protects database access behind clean API.
 */
public class BatchDAO {

    private static volatile boolean schemaInitialized = false;

    public BatchDAO() {
        ensureSchema();
    }

    /**
     * Automatically ensures that the 'batches' table and 'users.batch' column exist.
     */
    public synchronized void ensureSchema() {
        if (schemaInitialized) return;

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String dbProduct = conn.getMetaData().getDatabaseProductName().toLowerCase();
            boolean isSqlServer = dbProduct.contains("microsoft") || dbProduct.contains("sql server");

            try (Statement stmt = conn.createStatement()) {
                if (isSqlServer) {
                    stmt.execute(
                        "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'batches') " +
                        "BEGIN " +
                        "    CREATE TABLE dbo.batches ( " +
                        "        batch_id INT IDENTITY(1,1) PRIMARY KEY, " +
                        "        batch_name NVARCHAR(50) NOT NULL UNIQUE, " +
                        "        academic_year NVARCHAR(50), " +
                        "        description NVARCHAR(255), " +
                        "        status NVARCHAR(20) DEFAULT 'ACTIVE', " +
                        "        created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP " +
                        "    ); " +
                        "END"
                    );

                    stmt.execute(
                        "IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('users') AND name = 'batch') " +
                        "BEGIN " +
                        "    ALTER TABLE dbo.users ADD batch NVARCHAR(50) NULL; " +
                        "END"
                    );

                    stmt.execute(
                        "IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('users') AND name = 'department') " +
                        "BEGIN " +
                        "    ALTER TABLE dbo.users ADD department NVARCHAR(100) NULL; " +
                        "END"
                    );
                } else {
                    stmt.execute(
                        "CREATE TABLE IF NOT EXISTS batches ( " +
                        "    batch_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "    batch_name VARCHAR(50) NOT NULL UNIQUE, " +
                        "    academic_year VARCHAR(50), " +
                        "    description VARCHAR(255), " +
                        "    status VARCHAR(20) DEFAULT 'ACTIVE', " +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                        ");"
                    );

                    // Add columns to users if missing
                    try {
                        DatabaseMetaData md = conn.getMetaData();
                        ResultSet rs = md.getColumns(null, null, "users", "batch");
                        if (!rs.next()) {
                            stmt.execute("ALTER TABLE users ADD COLUMN batch VARCHAR(50) NULL;");
                        }
                        rs.close();

                        rs = md.getColumns(null, null, "users", "department");
                        if (!rs.next()) {
                            stmt.execute("ALTER TABLE users ADD COLUMN department VARCHAR(100) NULL;");
                        }
                        rs.close();
                    } catch (Exception ignored) {}
                }

                // Seed default batches if empty
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM batches");
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }
                rs.close();

                if (count == 0) {
                    String insertSql = "INSERT INTO batches (batch_name, academic_year, description, status) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                        String[][] defaults = {
                            {"2025/2026", "2025/2026", "Faculty of Technology 7th Intake (First Year)", "ACTIVE"},
                            {"2024/2025", "2024/2025", "Faculty of Technology 6th Intake (Second Year)", "ACTIVE"},
                            {"2023/2024", "2023/2024", "Faculty of Technology 5th Intake (Third Year)", "ACTIVE"},
                            {"2022/2023", "2022/2023", "Faculty of Technology 4th Intake (Final Year)", "ACTIVE"},
                            {"2021/2022", "2021/2022", "Faculty of Technology 3rd Intake (Graduating)", "ACTIVE"},
                            {"2020/2021", "2020/2021", "Faculty of Technology 2nd Intake (Alumni)", "COMPLETED"}
                        };

                        for (String[] def : defaults) {
                            ps.setString(1, def[0]);
                            ps.setString(2, def[1]);
                            ps.setString(3, def[2]);
                            ps.setString(4, def[3]);
                            ps.executeUpdate();
                        }
                    }
                }

                // Backfill existing undergraduates if batch is null
                stmt.executeUpdate("UPDATE users SET batch = '2021/2022' WHERE batch IS NULL AND username LIKE 'tg2021%'");
                stmt.executeUpdate("UPDATE users SET batch = '2024/2025' WHERE batch IS NULL AND username = 'Rashkan'");
            }
            schemaInitialized = true;
        } catch (Exception e) {
            System.err.println("[BatchDAO] Note on ensureSchema: " + e.getMessage());
        }
    }

    /**
     * Retrieves all batches ordered with active ones first.
     */
    public List<Batch> getAllBatches() throws DatabaseException {
        ensureSchema();
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT b.batch_id, b.batch_name, b.academic_year, b.description, b.status, b.created_at, " +
                     "  (SELECT COUNT(*) FROM users u WHERE u.batch = b.batch_name) AS student_count " +
                     "FROM batches b " +
                     "ORDER BY CASE WHEN b.status = 'ACTIVE' THEN 1 WHEN b.status = 'UPCOMING' THEN 2 ELSE 3 END, b.batch_name DESC";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Batch b = new Batch(
                        rs.getInt("batch_id"),
                        rs.getString("batch_name"),
                        rs.getString("academic_year"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getInt("student_count"),
                        rs.getString("created_at")
                    );
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch batches: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Retrieves all available batch names for dropdown selection.
     */
    public List<String> getAvailableBatchNames() {
        ensureSchema();
        List<String> names = new ArrayList<>();
        String sql = "SELECT batch_name FROM batches WHERE status IN ('ACTIVE', 'UPCOMING') ORDER BY batch_name DESC";

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String name = rs.getString("batch_name");
                    if (name != null && !name.trim().isEmpty()) {
                        names.add(name.trim());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[BatchDAO] Error loading batch names: " + e.getMessage());
        }

        // Fallbacks if empty
        if (names.isEmpty()) {
            names.add("2025/2026");
            names.add("2024/2025");
            names.add("2023/2024");
            names.add("2022/2023");
            names.add("2021/2022");
            names.add("2020/2021");
        }
        return names;
    }

    public Batch findById(int batchId) throws DatabaseException {
        ensureSchema();
        String sql = "SELECT b.batch_id, b.batch_name, b.academic_year, b.description, b.status, b.created_at, " +
                     "  (SELECT COUNT(*) FROM users u WHERE u.batch = b.batch_name) AS student_count " +
                     "FROM batches b WHERE b.batch_id = ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, batchId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Batch(
                            rs.getInt("batch_id"),
                            rs.getString("batch_name"),
                            rs.getString("academic_year"),
                            rs.getString("description"),
                            rs.getString("status"),
                            rs.getInt("student_count"),
                            rs.getString("created_at")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find batch by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean existsByName(String batchName) throws DatabaseException {
        ensureSchema();
        String sql = "SELECT COUNT(*) FROM batches WHERE batch_name = ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, batchName.trim());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check batch uniqueness: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean existsByNameAndNotId(String batchName, int batchId) throws DatabaseException {
        ensureSchema();
        String sql = "SELECT COUNT(*) FROM batches WHERE batch_name = ? AND batch_id != ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, batchName.trim());
                stmt.setInt(2, batchId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check batch uniqueness: " + e.getMessage(), e);
        }
        return false;
    }

    public Batch createBatch(Batch batch) throws DatabaseException {
        ensureSchema();
        String sql = "INSERT INTO batches (batch_name, academic_year, description, status) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, batch.getBatchName().trim());
                stmt.setString(2, batch.getAcademicYear() != null ? batch.getAcademicYear().trim() : batch.getBatchName().trim());
                stmt.setString(3, batch.getDescription() != null ? batch.getDescription().trim() : "");
                stmt.setString(4, batch.getStatus() != null ? batch.getStatus().trim() : "ACTIVE");

                int affected = stmt.executeUpdate();
                if (affected == 0) {
                    throw new DatabaseException("Creating batch failed, no rows affected.");
                }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        batch.setBatchId(keys.getInt(1));
                    }
                }
                return batch;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert batch: " + e.getMessage(), e);
        }
    }

    public boolean updateBatch(Batch batch) throws DatabaseException {
        ensureSchema();
        String sql = "UPDATE batches SET batch_name = ?, academic_year = ?, description = ?, status = ? WHERE batch_id = ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, batch.getBatchName().trim());
                stmt.setString(2, batch.getAcademicYear() != null ? batch.getAcademicYear().trim() : batch.getBatchName().trim());
                stmt.setString(3, batch.getDescription() != null ? batch.getDescription().trim() : "");
                stmt.setString(4, batch.getStatus() != null ? batch.getStatus().trim() : "ACTIVE");
                stmt.setInt(5, batch.getBatchId());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update batch: " + e.getMessage(), e);
        }
    }

    public boolean deleteBatch(int batchId) throws DatabaseException {
        ensureSchema();
        // Check if undergraduates are assigned to this batch
        Batch b = findById(batchId);
        if (b != null && b.getStudentCount() > 0) {
            throw new DatabaseException("Cannot delete batch '" + b.getBatchName() + "' because " + b.getStudentCount() + " undergraduates are currently assigned to it. Reassign students first or change status to COMPLETED.");
        }

        String sql = "DELETE FROM batches WHERE batch_id = ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, batchId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete batch: " + e.getMessage(), e);
        }
    }

    /**
     * Returns all undergraduate students belonging to a specified batch.
     */
    public List<Undergraduate> getUndergraduatesInBatch(String batchName) throws DatabaseException {
        ensureSchema();
        List<Undergraduate> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, r.description AS role_description " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.batch = ? " +
                     "ORDER BY u.first_name, u.last_name";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, batchName.trim());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Role role = new Role(rs.getInt("role_id"), rs.getString("role_name"), rs.getString("role_description"));
                        Undergraduate u = new Undergraduate(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            role,
                            rs.getString("contact_no"),
                            rs.getString("profile_pic"),
                            rs.getString("status")
                        );
                        try {
                            u.setBatch(rs.getString("batch"));
                        } catch (Exception ignored) {}
                        try {
                            u.setDepartment(rs.getString("department"));
                        } catch (Exception ignored) {}
                        list.add(u);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students in batch: " + e.getMessage(), e);
        }
        return list;
    }
}
