package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    /**
     * Resolves course_id (integer primary key) from either an integer string or course code (e.g. ICT2112).
     */
    private int resolveCourseId(Connection conn, String courseIdentifier) {
        if (courseIdentifier == null || courseIdentifier.trim().isEmpty()) {
            return 1;
        }
        try {
            return Integer.parseInt(courseIdentifier.trim());
        } catch (NumberFormatException e) {
            String sql = "SELECT course_id FROM courses WHERE UPPER(course_code) = UPPER(?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseIdentifier.trim());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("course_id");
                    }
                }
            } catch (SQLException ignored) {
            }
        }
        return 1;
    }

    /**
     * Saves a new course material.
     */
    public boolean saveMaterial(CourseMaterial material) throws DatabaseException, SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        int resolvedCourseId = resolveCourseId(conn, material.getCourseId());
        int uploadedBy = material.getUploadedBy() > 0 ? material.getUploadedBy() : 2; // Default to lecturer

        boolean hasUploadedBy = hasColumn(conn, "course_materials", "uploaded_by");

        String sql;
        if (hasUploadedBy) {
            sql = "INSERT INTO course_materials (course_id, uploaded_by, title, file_path) VALUES (?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO course_materials (course_id, title, file_path) VALUES (?, ?, ?)";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasUploadedBy) {
                stmt.setInt(1, resolvedCourseId);
                stmt.setInt(2, uploadedBy);
                stmt.setString(3, material.getTitle());
                stmt.setString(4, material.getFilePath());
            } else {
                stmt.setInt(1, resolvedCourseId);
                stmt.setString(2, material.getTitle());
                stmt.setString(3, material.getFilePath());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save course material: " + e.getMessage(), e);
        }
    }

    /**
     * Modifies an existing course material (Requirement 2: Modify materials).
     */
    public boolean updateMaterial(CourseMaterial material) throws DatabaseException, SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        int resolvedCourseId = resolveCourseId(conn, material.getCourseId());

        String sql = "UPDATE course_materials SET course_id = ?, title = ?, file_path = ? WHERE material_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resolvedCourseId);
            stmt.setString(2, material.getTitle());
            stmt.setString(3, material.getFilePath());
            stmt.setInt(4, material.getMaterialId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update course material: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a course material.
     */
    public boolean deleteMaterial(int materialId) throws DatabaseException, SQLException {
        String sql = "DELETE FROM course_materials WHERE material_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete course material: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves course materials filtered by course ID or code.
     */
    public List<CourseMaterial> getMaterialsByCourse(String courseId) throws DatabaseException, SQLException {
        List<CourseMaterial> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        String sql;
        boolean hasFilter = (courseId != null && !courseId.trim().isEmpty() && !courseId.equalsIgnoreCase("ALL"));
        int resolvedCourseId = hasFilter ? resolveCourseId(conn, courseId) : 0;

        if (hasFilter) {
            sql = "SELECT cm.*, c.course_code FROM course_materials cm " +
                  "LEFT JOIN courses c ON cm.course_id = c.course_id " +
                  "WHERE cm.course_id = ? OR UPPER(c.course_code) = UPPER(?) ORDER BY cm.material_id DESC";
        } else {
            sql = "SELECT cm.*, c.course_code FROM course_materials cm " +
                  "LEFT JOIN courses c ON cm.course_id = c.course_id ORDER BY cm.material_id DESC";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasFilter) {
                stmt.setInt(1, resolvedCourseId);
                stmt.setString(2, courseId.trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("material_id");
                    String courseCode = rs.getString("course_code");
                    String cId = (courseCode != null && !courseCode.isEmpty()) ? courseCode : String.valueOf(rs.getInt("course_id"));
                    String title = rs.getString("title");
                    String path = rs.getString("file_path");

                    int uploadedBy = 0;
                    try {
                        uploadedBy = rs.getInt("uploaded_by");
                    } catch (SQLException ignored) {
                    }

                    Timestamp timestamp = null;
                    try {
                        timestamp = rs.getTimestamp("uploaded_at");
                    } catch (SQLException ignored) {
                        try {
                            timestamp = rs.getTimestamp("created_at");
                        } catch (SQLException ignored2) {
                        }
                    }

                    CourseMaterial cm = new CourseMaterial(id, cId, uploadedBy, title, path, timestamp);
                    list.add(cm);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve course materials: " + e.getMessage(), e);
        }
        return list;
    }

    private boolean hasColumn(Connection conn, String tableName, String columnName) {
        try {
            DatabaseMetaData md = conn.getMetaData();
            try (ResultSet rs = md.getColumns(null, null, tableName, columnName)) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}