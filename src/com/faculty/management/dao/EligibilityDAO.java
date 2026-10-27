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

        String sql = "INSERT INTO eligibility (student_id, course_id, ca_percentage, is_eligible) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE ca_percentage = VALUES(ca_percentage), is_eligible = VALUES(is_eligible)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setDouble(3, caPercentage);
            stmt.setBoolean(4, isEligible);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Eligibility> getAllEligibilityRecords() throws DatabaseException, SQLException {
        List<Eligibility> list = new ArrayList<>();
        String sql = "SELECT * FROM eligibility ORDER BY student_id ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Eligibility e = new Eligibility(
                        rs.getInt("eligibility_id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getDouble("ca_percentage"),
                        rs.getBoolean("is_eligible"),
                        rs.getTimestamp("updated_at")
                );
                list.add(e);
            }
        }
        return list;
    }
}