package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for published semester Results (SGPA summaries).
 * Owned by Member 4. Individual course-level detail lives in {@link Grade}
 * rows and is attached to a Result by the service layer.
 */
public class ResultDAO {

    public void saveOrUpdateResult(Result result) throws DatabaseException {
        String sql = "INSERT INTO results (student_id, semester_number, academic_year, sgpa, " +
                     "credits_attempted, credits_completed) VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE sgpa = VALUES(sgpa), credits_attempted = VALUES(credits_attempted), " +
                     "credits_completed = VALUES(credits_completed)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, result.getStudentId());
            stmt.setInt(2, result.getSemesterNumber());
            stmt.setString(3, result.getAcademicYear());
            stmt.setDouble(4, result.getSgpa());
            stmt.setInt(5, result.getCreditsAttempted());
            stmt.setInt(6, result.getCreditsCompleted());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to publish result: " + e.getMessage(), e);
        }
    }

    public List<Result> findAllForStudent(int studentId) throws DatabaseException {
        String sql = "SELECT * FROM results WHERE student_id = ? ORDER BY academic_year, semester_number";
        List<Result> results = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch results: " + e.getMessage(), e);
        }
        return results;
    }

    /** Batch view: every student's published SGPA for one semester. */
    public List<Result> findBatchForSemester(int semesterNumber, String academicYear) throws DatabaseException {
        String sql = "SELECT r.*, u.first_name, u.last_name, u.username FROM results r " +
                     "JOIN users u ON r.student_id = u.user_id " +
                     "WHERE r.semester_number = ? AND r.academic_year = ? ORDER BY u.first_name";
        List<Result> results = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, semesterNumber);
            stmt.setString(2, academicYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Result result = mapRow(rs);
                    result.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch batch results: " + e.getMessage(), e);
        }
        return results;
    }

    private Result mapRow(ResultSet rs) throws SQLException {
        Result result = new Result(
                rs.getInt("student_id"), rs.getInt("semester_number"), rs.getString("academic_year"),
                rs.getDouble("sgpa"), rs.getInt("credits_attempted"), rs.getInt("credits_completed"));
        result.setResultId(rs.getInt("result_id"));
        return result;
    }
}
