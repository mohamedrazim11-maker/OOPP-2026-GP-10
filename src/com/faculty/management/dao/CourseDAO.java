package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course entity operations using JDBC.
 * Demonstrates Database Handling, Abstraction, Polymorphism, and Exception Handling.
 */
public class CourseDAO {

    /**
     * Inserts a new course record into the courses table.
     *
     * @param course Course entity to insert
     * @return Saved Course entity with generated courseId
     * @throws DatabaseException If database operation fails
     */
    public Course createCourse(Course course) throws DatabaseException {
        String sql = "INSERT INTO courses (course_code, course_name, credit_value, theory_hours, practical_hours, department, semester, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCreditValue());
            stmt.setInt(4, course.getTheoryHours());
            stmt.setInt(5, course.getPracticalHours());
            stmt.setString(6, course.getDepartment());
            stmt.setString(7, course.getSemester());
            stmt.setString(8, course.getDescription());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating course failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setCourseId(generatedKeys.getInt(1));
                }
            }

            return course;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert course into database: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all courses ordered by course code.
     *
     * @return List of Course entities
     * @throws DatabaseException If database operation fails
     */
    public List<Course> getAllCourses() throws DatabaseException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name, credit_value, theory_hours, practical_hours, " +
                     "department, semester, description FROM courses ORDER BY course_code ASC";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch courses from database: " + e.getMessage(), e);
        }

        return courses;
    }

    /**
     * Finds a course by unique course ID.
     *
     * @param courseId ID of the course
     * @return Course entity or null if not found
     * @throws DatabaseException If database operation fails
     */
    public Course findById(int courseId) throws DatabaseException {
        String sql = "SELECT course_id, course_code, course_name, credit_value, theory_hours, practical_hours, " +
                     "department, semester, description FROM courses WHERE course_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find course by ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Finds a course by unique course code.
     *
     * @param courseCode Course code (e.g. "ICT2132")
     * @return Course entity or null if not found
     * @throws DatabaseException If database operation fails
     */
    public Course findByCode(String courseCode) throws DatabaseException {
        String sql = "SELECT course_id, course_code, course_name, credit_value, theory_hours, practical_hours, " +
                     "department, semester, description FROM courses WHERE course_code = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find course by code: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Checks if a course exists with the given course code.
     *
     * @param courseCode Code to check
     * @return true if exists, false otherwise
     * @throws DatabaseException If database operation fails
     */
    public boolean existsByCode(String courseCode) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_code = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check course code existence: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Checks if a course code already exists for another course (excluding a given course ID).
     */
    public boolean existsByCodeExcludingCourse(String courseCode, int courseId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_code = ? AND course_id != ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check course code uniqueness: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Updates an existing course record in the database.
     *
     * @param course Course entity with updated values
     * @return true if updated successfully
     * @throws DatabaseException If database operation fails
     */
    public boolean updateCourse(Course course) throws DatabaseException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credit_value = ?, theory_hours = ?, " +
                     "practical_hours = ?, department = ?, semester = ?, description = ? WHERE course_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCreditValue());
            stmt.setInt(4, course.getTheoryHours());
            stmt.setInt(5, course.getPracticalHours());
            stmt.setString(6, course.getDepartment());
            stmt.setString(7, course.getSemester());
            stmt.setString(8, course.getDescription());
            stmt.setInt(9, course.getCourseId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("No course found with ID " + course.getCourseId() + " to update.");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update course in database: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a course by unique course ID.
     *
     * @param courseId ID of the course to delete
     * @return true if deleted
     * @throws DatabaseException If database operation fails
     */
    public boolean deleteCourse(int courseId) throws DatabaseException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("No course found with ID " + courseId + " to delete.");
            }
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || (e.getMessage() != null && e.getMessage().contains("foreign key constraint"))) {
                throw new DatabaseException("Cannot delete course because student marks, attendance, or materials exist for it.", e);
            }
            throw new DatabaseException("Failed to delete course: " + e.getMessage(), e);
        }
    }

    /**
     * Polymorphic overload to delete a course using a Course entity.
     *
     * @param course Course entity to delete
     * @return true if deleted
     * @throws DatabaseException If database operation fails
     */
    public boolean deleteCourse(Course course) throws DatabaseException {
        if (course == null) {
            throw new DatabaseException("Course entity cannot be null.");
        }
        return deleteCourse(course.getCourseId());
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setCreditValue(rs.getInt("credit_value"));
        c.setTheoryHours(rs.getInt("theory_hours"));
        c.setPracticalHours(rs.getInt("practical_hours"));
        c.setDepartment(rs.getString("department"));
        c.setSemester(rs.getString("semester"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}
