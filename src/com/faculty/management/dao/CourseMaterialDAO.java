package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    public boolean saveMaterial(CourseMaterial material) throws DatabaseException, SQLException {
        String sql = "INSERT INTO course_materials (course_id, title, file_path) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, material.getCourseId());
            stmt.setString(2, material.getTitle());
            stmt.setString(3, material.getFilePath());

            return stmt.executeUpdate() > 0;
        }
    }

    public List<CourseMaterial> getMaterialsByCourse(String courseId) throws DatabaseException, SQLException {
        List<CourseMaterial> list = new ArrayList<>();
        // Querying without relying on specific timestamp column names in ORDER BY
        String sql = "SELECT * FROM course_materials WHERE course_id = ? ORDER BY material_id DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Safely read material details
                int id = rs.getInt("material_id");
                String cId = rs.getString("course_id");
                String title = rs.getString("title");
                String path = rs.getString("file_path");

                // Try fetching timestamp safely if it exists under another column name, otherwise null
                Timestamp timestamp = null;
                try {
                    timestamp = rs.getTimestamp("uploaded_at");
                } catch (SQLException ignored) {
                    try {
                        timestamp = rs.getTimestamp("created_at");
                    } catch (SQLException ignored2) {
                        // If no timestamp column exists in your SQL table, it defaults to null
                    }
                }

                CourseMaterial cm = new CourseMaterial(id, cId, title, path, timestamp);
                list.add(cm);
            }
        }
        return list;
    }
}