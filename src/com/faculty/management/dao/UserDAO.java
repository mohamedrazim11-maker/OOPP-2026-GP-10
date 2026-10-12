package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Admin;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.Role;
import com.faculty.management.model.TechnicalOfficer;
import com.faculty.management.model.Undergraduate;
import com.faculty.management.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for User operations using pure JDBC.
 */
public class UserDAO {

    /**
     * Inserts a new user record into the users table.
     *
     * @param user The User subclass object (Admin, Lecturer, TechnicalOfficer, Undergraduate)
     * @return The saved User object with generated userId
     * @throws DatabaseException If a database error occurs during insert
     */
    public User createUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, password, email, first_name, last_name, role_id, contact_no, profile_pic, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setInt(6, user.getRole() != null ? user.getRole().getRoleId() : 4);
            stmt.setString(7, user.getContactNo());
            stmt.setString(8, user.getProfilePic());
            stmt.setString(9, user.getStatus() != null ? user.getStatus() : "ACTIVE");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }

            return user;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert user into database: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a user by username, joining the role information.
     *
     * @param username The username to look up
     * @return Concrete User instance (Admin, Lecturer, TechnicalOfficer, Undergraduate) or null if not found
     * @throws DatabaseException If a database error occurs
     */
    public User findByUsername(String username) throws DatabaseException {
        String sql = "SELECT u.user_id, u.username, u.password, u.email, u.first_name, u.last_name, " +
                     "u.role_id, u.contact_no, u.profile_pic, u.status, " +
                     "r.role_name, r.description AS role_description " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.username = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user by username: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Helper method to map a database ResultSet row to the appropriate User subclass (Polymorphism).
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        int roleId = rs.getInt("role_id");
        String roleName = rs.getString("role_name");
        String roleDesc = rs.getString("role_description");
        String contactNo = rs.getString("contact_no");
        String profilePic = rs.getString("profile_pic");
        String status = rs.getString("status");

        Role role = new Role(roleId, roleName, roleDesc);
        User user;

        if (Role.ADMIN.equalsIgnoreCase(roleName)) {
            user = new Admin(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
        } else if (Role.LECTURER.equalsIgnoreCase(roleName)) {
            user = new Lecturer(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
        } else if (Role.TECHNICAL_OFFICER.equalsIgnoreCase(roleName)) {
            user = new TechnicalOfficer(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
        } else if (Role.UNDERGRADUATE.equalsIgnoreCase(roleName) || "STUDENT".equalsIgnoreCase(roleName)) {
            user = new Undergraduate(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
        } else {
            user = new Undergraduate(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
        }

        return user;
    }
}
