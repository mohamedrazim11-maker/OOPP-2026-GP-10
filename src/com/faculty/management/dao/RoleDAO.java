package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Role operations using JDBC.
 * Demonstrates Database Handling, Encapsulation, and Exception Handling.
 */
public class RoleDAO {

    /**
     * Retrieves all roles from the database.
     *
     * @return List of Role objects
     * @throws DatabaseException If database access fails
     */
    public List<Role> getAllRoles() throws DatabaseException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT role_id, role_name, description FROM roles ORDER BY role_id ASC";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Role role = new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("description")
                );
                roles.add(role);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch roles from database: " + e.getMessage(), e);
        }

        return roles;
    }

    /**
     * Finds a role by its unique role ID.
     *
     * @param roleId The primary key ID of the role
     * @return Role object or null if not found
     * @throws DatabaseException If database error occurs
     */
    public Role findById(int roleId) throws DatabaseException {
        String sql = "SELECT role_id, role_name, description FROM roles WHERE role_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Role(
                            rs.getInt("role_id"),
                            rs.getString("role_name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch role by ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Finds a role by its role name (e.g. ADMIN, LECTURER).
     *
     * @param roleName The name of the role
     * @return Role object or null if not found
     * @throws DatabaseException If database error occurs
     */
    public Role findByName(String roleName) throws DatabaseException {
        String sql = "SELECT role_id, role_name, description FROM roles WHERE UPPER(role_name) = UPPER(?)";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roleName.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Role(
                            rs.getInt("role_id"),
                            rs.getString("role_name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch role by name: " + e.getMessage(), e);
        }

        return null;
    }
}
