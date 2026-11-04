package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Eligibility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EligibilityDAO {

    public boolean saveOrUpdateEligibility(int studentId, int courseId, double caPercentage, boolean isEligible)
            throws DatabaseException, SQLException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        // 1. Try UPDATE first
        String updateSql = "UPDATE eligibility SET ca_percentage = ?, is_eligible = ?, updated_at = CURRENT_TIMESTAMP WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement uStmt = conn.prepareStatement(updateSql)) {
            uStmt.setDouble(1, caPercentage);
            uStmt.setBoolean(2, isEligible);
            uStmt.setInt(3, studentId);
            uStmt.setInt(4, courseId);
            if (uStmt.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ignored) {
            // If updated_at column is missing, retry with basic columns
            String fallbackUpdate = "UPDATE eligibility SET ca_percentage = ?, is_eligible = ? WHERE student_id = ? AND course_id = ?";
            try (PreparedStatement fuStmt = conn.prepareStatement(fallbackUpdate)) {
                fuStmt.setDouble(1, caPercentage);
                fuStmt.setBoolean(2, isEligible);
                fuStmt.setInt(3, studentId);
                fuStmt.setInt(4, courseId);
                if (fuStmt.executeUpdate() > 0) {
                    return true;
                }
            }
        }

        // 2. Perform INSERT if no record was updated
        String insertSql = "INSERT INTO eligibility (student_id, course_id, ca_percentage, is_eligible) VALUES (?, ?, ?, ?)";
        try (PreparedStatement iStmt = conn.prepareStatement(insertSql)) {
            iStmt.setInt(1, studentId);
            iStmt.setInt(2, courseId);
            iStmt.setDouble(3, caPercentage);
            iStmt.setBoolean(4, isEligible);
            return iStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save eligibility record: " + e.getMessage(), e);
        }
    }

    public List<Eligibility> getAllEligibilityRecords() throws DatabaseException, SQLException {
        List<Eligibility> list = new ArrayList<>();
        String sql = "SELECT * FROM eligibility ORDER BY student_id ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = null;
                try {
                    ts = rs.getTimestamp("updated_at");
                } catch (SQLException ignored) {
                }
                Eligibility e = new Eligibility(
                        rs.getInt("eligibility_id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getDouble("ca_percentage"),
                        rs.getBoolean("is_eligible"),
                        ts
                );
                list.add(e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve eligibility records: " + e.getMessage(), e);
        }
        return list;
    }
}
