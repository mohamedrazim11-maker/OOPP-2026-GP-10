package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the shared Course reference table.
 * Read-only from Member 4's perspective — Course creation/editing belongs
 * to Member 1 (Admin & User Management).
 */
public class CourseDAO {

    public Course findById(int courseId) throws DatabaseException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch course: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Course> findAll() throws DatabaseException {
        String sql = "SELECT * FROM courses ORDER BY academic_year DESC, semester_number, course_code";
        List<Course> courses = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch courses: " + e.getMessage(), e);
        }
        return courses;
    }

    Course mapRow(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("course_id"),
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getInt("credits"),
                rs.getInt("theory_sessions"),
                rs.getInt("practical_sessions"),
                rs.getInt("semester_number"),
                rs.getString("academic_year"),
                rs.getString("department")
        );
    }
}
