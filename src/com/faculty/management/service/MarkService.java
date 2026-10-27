package com.faculty.management.service;

import com.faculty.management.dao.MarkDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.model.Mark;

import java.sql.SQLException;
import java.util.List;

public class MarkService {
    private final MarkDAO markDAO = new MarkDAO();

    /**
     * Instantiates Mark (which validates 0-100 range) and persists to DB.
     */
    public boolean saveOrUpdateMark(String studentId, int evaluationId, double marks)
            throws InvalidMarkException, DatabaseException, SQLException {

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }

        // Creating Mark instance triggers its validation constructor
        Mark mark = new Mark(studentId, evaluationId, marks);

        // Try updating first; if no record was modified, perform insert
        boolean updated = markDAO.updateMark(mark);
        if (!updated) {
            return markDAO.insertMark(mark);
        }
        return true;
    }

    public List<Mark> getMarksForStudent(String studentId) throws DatabaseException, SQLException, InvalidMarkException {
        return markDAO.getMarksByStudent(studentId);
    }

    public List<Mark> getMarksForEvaluation(int evaluationId) throws DatabaseException, SQLException, InvalidMarkException {
        return markDAO.getMarksByEvaluation(evaluationId);
    }

    public boolean removeMark(String studentId, int evaluationId) throws DatabaseException, SQLException {
        return markDAO.deleteMark(studentId, evaluationId);
    }
}