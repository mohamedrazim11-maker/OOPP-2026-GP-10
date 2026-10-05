package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Medical;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public boolean addMedical(Medical medical) throws SQLException, DatabaseException {
        String query = "INSERT INTO medical (student_id, course_id, session_id, medical_date, reason, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, medical.getStudentId());
            stmt.setString(2, medical.getCourseId());
            if (medical.getSessionId() != null) {
                stmt.setInt(3, medical.getSessionId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setDate(4, Date.valueOf(medical.getMedicalDate()));
            stmt.setString(5, medical.getReason());
            stmt.setString(6, medical.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateMedicalStatus(int medicalId, String status) throws SQLException, DatabaseException {
        String query = "UPDATE medical SET status = ? WHERE medical_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, medicalId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Medical> getMedicalsByStudent(int studentId) throws SQLException, DatabaseException {
        List<Medical> list = new ArrayList<>();
        String query = "SELECT * FROM medical WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Medical(
                            rs.getInt("medical_id"),
                            rs.getInt("student_id"),
                            rs.getString("course_id"),
                            (Integer) rs.getObject("session_id"),
                            rs.getDate("medical_date").toLocalDate(),
                            rs.getString("reason"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }
}
