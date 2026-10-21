package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.model.Mark;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkDAO {

    /**
     * Inserts a new student mark entry into the database.
     */
    public boolean insertMark(Mark mark) throws DatabaseException, SQLException {
        String sql = "INSERT INTO marks (student_id, evaluation_id, marks_obtained) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mark.getStudentId());
            stmt.setInt(2, mark.getEvaluationId());
            stmt.setDouble(3, mark.getMarksObtained());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing mark entry.
     */
    public boolean updateMark(Mark mark) throws DatabaseException, SQLException {
        String sql = "UPDATE marks SET marks_obtained = ? WHERE student_id = ? AND evaluation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, mark.getMarksObtained());
            stmt.setString(2, mark.getStudentId());
            stmt.setInt(3, mark.getEvaluationId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Fetches marks for a specific student across all evaluations.
     */
    public List<Mark> getMarksByStudent(String studentId) throws DatabaseException, SQLException, InvalidMarkException {
        List<Mark> marksList = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Mark mark = new Mark(
                        rs.getString("student_id"),
                        rs.getInt("evaluation_id"),
                        rs.getDouble("marks_obtained")
                );
                marksList.add(mark);
            }
        }
        return marksList;
    }

    /**
     * Fetches all marks for a given evaluation ID.
     */
    public List<Mark> getMarksByEvaluation(int evaluationId) throws DatabaseException, SQLException, InvalidMarkException {
        List<Mark> marksList = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE evaluation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, evaluationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Mark mark = new Mark(
                        rs.getString("student_id"),
                        rs.getInt("evaluation_id"),
                        rs.getDouble("marks_obtained")
                );
                marksList.add(mark);
            }
        }
        return marksList;
    }

    /**
     * Deletes a student mark entry.
     */
    public boolean deleteMark(String studentId, int evaluationId) throws DatabaseException, SQLException {
        String sql = "DELETE FROM marks WHERE student_id = ? AND evaluation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.setInt(2, evaluationId);

            return stmt.executeUpdate() > 0;
        }
    }
}