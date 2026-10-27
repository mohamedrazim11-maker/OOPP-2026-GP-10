package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Lecturer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerDAO {

    public boolean updateLecturerProfile(Lecturer lecturer) throws DatabaseException, SQLException {
        String sql = "UPDATE users u " +
                "LEFT JOIN lecturer l ON u.user_id = l.user_id " +
                "SET u.contact_no = ?, u.profile_pic = ?, l.department = ? " +
                "WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, lecturer.getContactNo());
            stmt.setString(2, lecturer.getProfilePic());
            stmt.setString(3, lecturer.getDepartment());
            stmt.setInt(4, lecturer.getUserId());

            return stmt.executeUpdate() > 0;
        }
    }

    public Lecturer getLecturerById(int userId) throws DatabaseException, SQLException {
        String sql = "SELECT u.*, l.department FROM users u " +
                "LEFT JOIN lecturer l ON u.user_id = l.user_id " +
                "WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

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
                lec.setDepartment(rs.getString("department"));
                return lec;
            }
        }
        return null;
    }
}