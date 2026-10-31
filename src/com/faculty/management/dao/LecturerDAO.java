package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;
import com.faculty.management.model.Lecturer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    /**
     * Updates lecturer profile information in the users table.
     * Username and password cannot be modified by the lecturer per domain rules.
     */
    public boolean updateLecturerProfile(Lecturer lecturer) throws DatabaseException, SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, contact_no = ?, profile_pic = ? " +
                "WHERE user_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lecturer.getFirstName());
            stmt.setString(2, lecturer.getLastName());
            stmt.setString(3, lecturer.getEmail());
            stmt.setString(4, lecturer.getContactNo());
            stmt.setString(5, lecturer.getProfilePic());
            stmt.setInt(6, lecturer.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update lecturer profile: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves lecturer details from users table and associated department from assigned courses.
     */
    public Lecturer getLecturerById(int userId) throws DatabaseException, SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Lecturer lec = new Lecturer();
                    lec.setUserId(rs.getInt("user_id"));
                    lec.setUsername(rs.getString("username"));
                    lec.setEmail(rs.getString("email"));
                    lec.setFirstName(rs.getString("first_name"));
                    lec.setLastName(rs.getString("last_name"));
                    lec.setContactNo(rs.getString("contact_no"));
                    lec.setProfilePic(rs.getString("profile_pic"));
                    lec.setStatus(rs.getString("status"));

                    // Fetch department from assigned course if available
                    String dept = getLecturerDepartment(conn, userId);
                    lec.setDepartment(dept != null ? dept : "Faculty of Technology");
                    return lec;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch lecturer by ID: " + e.getMessage(), e);
        }
        return null;
    }

    private String getLecturerDepartment(Connection conn, int lecturerId) {
        String sql = "SELECT c.department FROM courses c " +
                "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                "WHERE cl.lecturer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("department");
                }
            }
        } catch (SQLException ignored) {
            // Table course_lecturers might be empty or unseeded
        }
        return null;
    }

    /**
     * Retrieves courses assigned to this lecturer via course_lecturers table.
     * Falls back to all courses if no specific mapping is found.
     */
    public List<Course> getAssignedCourses(int lecturerId) throws DatabaseException, SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit_value, c.department " +
                "FROM courses c " +
                "JOIN course_lecturers cl ON c.course_id = cl.course_id " +
                "WHERE cl.lecturer_id = ? ORDER BY c.course_code ASC";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setCourseId(rs.getInt("course_id"));
                    c.setCourseCode(rs.getString("course_code"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCreditValue(rs.getInt("credit_value"));
                    c.setDepartment(rs.getString("department"));
                    courses.add(c);
                }
            }
        } catch (SQLException ignored) {
            // fallback below
        }

        // If no assigned courses found via course_lecturers, return all courses as default fallback
        if (courses.isEmpty()) {
            String fallbackSql = "SELECT course_id, course_code, course_name, credit_value, department FROM courses ORDER BY course_code ASC";
            try (PreparedStatement stmt = conn.prepareStatement(fallbackSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setCourseId(rs.getInt("course_id"));
                    c.setCourseCode(rs.getString("course_code"));
                    c.setCourseName(rs.getString("course_name"));
                    c.setCreditValue(rs.getInt("credit_value"));
                    c.setDepartment(rs.getString("department"));
                    courses.add(c);
                }
            }
        }
        return courses;
    }
}