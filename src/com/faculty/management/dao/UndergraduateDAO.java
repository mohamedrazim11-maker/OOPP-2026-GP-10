package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Role;
import com.faculty.management.model.Undergraduate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Undergraduate (Student) records.
 * Per the spec, a student may only self-update their contact number and
 * profile picture — never their username, password, or academic data —
 * so this DAO deliberately exposes no generic "update everything" method.
 */
public class UndergraduateDAO {

    public Undergraduate findById(int studentId) throws DatabaseException {
        String sql = "SELECT u.*, r.role_name, r.description AS role_description FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id WHERE u.user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student profile: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Undergraduate> findAll() throws DatabaseException {
        String sql = "SELECT u.*, r.role_name, r.description AS role_description FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id WHERE r.role_name = 'UNDERGRADUATE' " +
                     "ORDER BY u.first_name, u.last_name";
        List<Undergraduate> students = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students: " + e.getMessage(), e);
        }
        return students;
    }

    /**
     * Updates ONLY the contact number and profile picture path for a
     * student — enforces "cannot modify username, password, or academic
     * information" at the data-access layer, not just in the GUI.
     */
    public void updateContactAndPicture(int studentId, String contactNo, String profilePicPath) throws DatabaseException {
        String sql = "UPDATE users SET contact_no = ?, profile_pic = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, contactNo);
            stmt.setString(2, profilePicPath);
            stmt.setInt(3, studentId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException("No student found with id " + studentId + " to update.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update student profile: " + e.getMessage(), e);
        }
    }

    private Undergraduate mapRow(ResultSet rs) throws SQLException {
        Role role = new Role(rs.getInt("role_id"), rs.getString("role_name"), rs.getString("role_description"));
        return new Undergraduate(
                rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("email"),
                rs.getString("first_name"), rs.getString("last_name"), role, rs.getString("contact_no"),
                rs.getString("profile_pic"), rs.getString("status"));
    }
}
