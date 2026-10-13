package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the shared Course reference table.
 * Read-only from Member 4's perspective — Course creation/editing belongs
 * to Member 1 (Admin & User Management).
 */
public class CourseDAO {

    public Course createCourse(Course course) throws DatabaseException {
        String sql = "INSERT INTO courses (course_code, course_name, credits, theory_sessions, practical_sessions, " +
                     "semester_number, academic_year, department, semester, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getTheorySessions());
            stmt.setInt(5, course.getPracticalSessions());
            stmt.setInt(6, course.getSemesterNumber());
            stmt.setString(7, course.getAcademicYear());
            stmt.setString(8, course.getDepartment());
            stmt.setString(9, "Semester " + course.getSemesterNumber());
            stmt.setString(10, null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating course failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setCourseId(generatedKeys.getInt(1));
                }
            }
            return course;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert course: " + e.getMessage(), e);
        }
    }

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

    public Course findByCode(String courseCode) throws DatabaseException {
        String sql = "SELECT * FROM courses WHERE LOWER(course_code) = LOWER(?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch course by code: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Course> findAll() throws DatabaseException {
        return getAllCourses();
    }

    public List<Course> getAllCourses() throws DatabaseException {
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

    public boolean existsByCode(String courseCode) throws DatabaseException {
        String sql = "SELECT 1 FROM courses WHERE LOWER(course_code) = LOWER(?) LIMIT 1";
        return existsBy(sql, courseCode);
    }

    public boolean existsByCodeExcludingCourse(String courseCode, int courseId) throws DatabaseException {
        String sql = "SELECT 1 FROM courses WHERE LOWER(course_code) = LOWER(?) AND course_id <> ? LIMIT 1";
        return existsBy(sql, courseCode, courseId);
    }

    public boolean updateCourse(Course course) throws DatabaseException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, theory_sessions = ?, " +
                     "practical_sessions = ?, semester_number = ?, academic_year = ?, department = ? WHERE course_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getTheorySessions());
            stmt.setInt(5, course.getPracticalSessions());
            stmt.setInt(6, course.getSemesterNumber());
            stmt.setString(7, course.getAcademicYear());
            stmt.setString(8, course.getDepartment());
            stmt.setInt(9, course.getCourseId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update course: " + e.getMessage(), e);
        }
    }

    public boolean deleteCourse(int courseId) throws DatabaseException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete course: " + e.getMessage(), e);
        }
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

    private boolean existsBy(String sql, String value) throws DatabaseException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to validate course uniqueness: " + e.getMessage(), e);
        }
    }

    private boolean existsBy(String sql, String value, int courseId) throws DatabaseException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to validate course uniqueness: " + e.getMessage(), e);
        }
    }
}
