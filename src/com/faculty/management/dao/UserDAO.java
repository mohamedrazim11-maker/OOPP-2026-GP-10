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
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations using pure JDBC.
 */
public class UserDAO {

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

    public List<User> getAllUsers() throws DatabaseException {
        String sql = "SELECT u.user_id, u.username, u.password, u.email, u.first_name, u.last_name, " +
                     "u.role_id, u.contact_no, u.profile_pic, u.status, r.role_name, r.description AS role_description " +
                     "FROM users u JOIN roles r ON u.role_id = r.role_id ORDER BY u.user_id ASC";

        List<User> users = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users: " + e.getMessage(), e);
        }

        return users;
    }

    public User findById(int userId) throws DatabaseException {
        String sql = "SELECT u.user_id, u.username, u.password, u.email, u.first_name, u.last_name, " +
                     "u.role_id, u.contact_no, u.profile_pic, u.status, r.role_name, r.description AS role_description " +
                     "FROM users u JOIN roles r ON u.role_id = r.role_id WHERE u.user_id = ?";

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

    public boolean existsByUsername(String username) throws DatabaseException {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?) LIMIT 1";
        return hasSingleMatch(sql, username);
    }

    public boolean existsByEmail(String email) throws DatabaseException {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";
        return hasSingleMatch(sql, email);
    }

    public boolean existsByEmailExcludingUser(String email, int userId) throws DatabaseException {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) AND user_id <> ? LIMIT 1";
        return hasSingleMatch(sql, email, userId);
    }

    public boolean existsByUsernameExcludingUser(String username, int userId) throws DatabaseException {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?) AND user_id <> ? LIMIT 1";
        return hasSingleMatch(sql, username, userId);
    }

    public boolean updateUser(User user) throws DatabaseException {
        String sql = "UPDATE users SET username = ?, email = ?, first_name = ?, last_name = ?, " +
                     "role_id = ?, contact_no = ?, profile_pic = ?, status = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setInt(5, user.getRole() != null ? user.getRole().getRoleId() : 4);
            stmt.setString(6, user.getContactNo());
            stmt.setString(7, user.getProfilePic());
            stmt.setString(8, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            stmt.setInt(9, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public boolean updateCredentials(int userId, String username, String password) throws DatabaseException {
        String sql = "UPDATE users SET username = ?, password = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update credentials: " + e.getMessage(), e);
        }
    }

    public boolean updateUsername(int userId, String username) throws DatabaseException {
        String sql = "UPDATE users SET username = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update username: " + e.getMessage(), e);
        }
    }

    public boolean updatePassword(int userId, String password) throws DatabaseException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update password: " + e.getMessage(), e);
        }
    }

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

    public boolean updateUserRole(int userId, int roleId) throws DatabaseException {
        String sql = "UPDATE users SET role_id = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user role: " + e.getMessage(), e);
        }
    }

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

    private boolean hasSingleMatch(String sql, String value) throws DatabaseException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to validate unique user data: " + e.getMessage(), e);
        }
    }

    private boolean hasSingleMatch(String sql, String value, int userId) throws DatabaseException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to validate unique user data: " + e.getMessage(), e);
        }
    }

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
