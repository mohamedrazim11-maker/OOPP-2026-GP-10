package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations using pure JDBC.
 * Demonstrates Database Handling, Abstraction, Polymorphism, and Exception Handling.
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
     * Finds a user by unique user ID.
     *
     * @param userId The ID of the user
     * @return User subclass instance or null
     * @throws DatabaseException If database error occurs
     */
    public User findById(int userId) throws DatabaseException {
        String sql = "SELECT u.user_id, u.username, u.password, u.email, u.first_name, u.last_name, " +
                     "u.role_id, u.contact_no, u.profile_pic, u.status, " +
                     "r.role_name, r.description AS role_description " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.user_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user by ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Checks if a user already exists with the given username.
     *
     * @param username Username to check
     * @return true if exists, false otherwise
     * @throws DatabaseException If database error occurs
     */
    public boolean existsByUsername(String username) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check username existence: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Checks if a user already exists with the given email.
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     * @throws DatabaseException If database error occurs
     */
    public boolean existsByEmail(String email) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check email existence: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return List of polymorphic User objects
     * @throws DatabaseException If database error occurs
     */
    public List<User> getAllUsers() throws DatabaseException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.password, u.email, u.first_name, u.last_name, " +
                     "u.role_id, u.contact_no, u.profile_pic, u.status, " +
                     "r.role_name, r.description AS role_description " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "ORDER BY u.user_id ASC";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all users: " + e.getMessage(), e);
        }

        return userList;
    }

    /**
     * Updates an existing user's information.
     *
     * @param user The user object with updated fields
     * @return true if updated successfully, false otherwise
     * @throws DatabaseException If database error occurs
     */
    public boolean updateUser(User user) throws DatabaseException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, contact_no = ?, role_id = ?, status = ? " +
                     "WHERE user_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getContactNo());
            stmt.setInt(5, user.getRole() != null ? user.getRole().getRoleId() : 4);
            stmt.setString(6, user.getStatus());
            stmt.setInt(7, user.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a user's credentials (username and password).
     *
     * @param userId      ID of the user
     * @param username    New username
     * @param newPassword New password
     * @return true if updated
     * @throws DatabaseException If database error occurs
     */
    public boolean updateCredentials(int userId, String username, String newPassword) throws DatabaseException {
        String sql = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, newPassword);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user credentials: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a user's status (ACTIVE, INACTIVE, SUSPENDED).
     *
     * @param userId The ID of the user
     * @param status New status
     * @return true if successful
     * @throws DatabaseException If database error occurs
     */
    public boolean updateStatus(int userId, String status) throws DatabaseException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user status: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId The ID of the user to delete
     * @return true if deleted
     * @throws DatabaseException If database error occurs
     */
    public boolean deleteUser(int userId) throws DatabaseException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        }
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
        } else {
            // Default to Undergraduate / Student
            user = new Undergraduate(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
        }

        return user;
    }
}
