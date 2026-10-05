package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.model.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean addAttendance(Attendance attendance) throws SQLException {
        String query = "INSERT INTO attendance (session_id, student_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, attendance.getSessionId());
            stmt.setInt(2, attendance.getStudentId());
            stmt.setString(3, attendance.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateAttendance(Attendance attendance) throws SQLException {
        String query = "UPDATE attendance SET status = ? WHERE record_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, attendance.getStatus());
            stmt.setInt(2, attendance.getRecordId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAttendance(int recordId) throws SQLException {
        String query = "DELETE FROM attendance WHERE record_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recordId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Attendance> getAttendanceBySession(int sessionId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE session_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Attendance(
                            rs.getInt("record_id"),
                            rs.getInt("session_id"),
                            rs.getInt("student_id"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }
}
