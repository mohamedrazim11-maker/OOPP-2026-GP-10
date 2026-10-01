package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.MedicalSubmission;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class MedicalSubmissionDAO {
    public void create(int studentId, Integer courseId, LocalDate start, LocalDate end, String reason, String documentPath) throws DatabaseException {
        String sql = "INSERT INTO medical_submissions (student_id, course_id, start_date, end_date, reason, document_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId); if (courseId == null) stmt.setNull(2, Types.INTEGER); else stmt.setInt(2, courseId);
            stmt.setDate(3, java.sql.Date.valueOf(start)); stmt.setDate(4, java.sql.Date.valueOf(end)); stmt.setString(5, reason); stmt.setString(6, documentPath);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new DatabaseException("Failed to submit medical request: " + e.getMessage(), e); }
    }
    public List<MedicalSubmission> findByStudent(int studentId) throws DatabaseException {
        String sql = "SELECT m.*, c.course_code FROM medical_submissions m LEFT JOIN courses c ON c.course_id=m.course_id WHERE m.student_id=? ORDER BY m.submitted_at DESC";
        List<MedicalSubmission> records = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId); try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) records.add(new MedicalSubmission(
                    rs.getInt("medical_id"), rs.getString("course_code"), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(),
                    rs.getString("reason"), rs.getString("document_path"), rs.getString("status")));
            }
        } catch (SQLException e) { throw new DatabaseException("Failed to load medical requests: " + e.getMessage(), e); }
        return records;
    }
}
