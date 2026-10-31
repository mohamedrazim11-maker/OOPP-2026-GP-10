package com.faculty.management.service;

import com.faculty.management.dao.MarkDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.model.Evaluation;
import com.faculty.management.model.Mark;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer coordinating marks validation and calculations.
 * Enforces business rules: All marks must be out of 100, CA >= 40% threshold.
 */
public class MarkService {
    private final MarkDAO markDAO = new MarkDAO();
    private final EvaluationCalculator caCalculator = new CAEligibilityCalculator();
    private final EvaluationCalculator finalCalculator = new FinalMarkCalculator();

    /**
     * Instantiates Mark (which validates 0-100 range) and persists to DB.
     */
    public boolean saveOrUpdateMark(String studentId, int evaluationId, double marks)
            throws InvalidMarkException, DatabaseException, SQLException {

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }

        // Creating Mark instance triggers its validation constructor (Requirement 4: Out of 100)
        Mark mark = new Mark(studentId, evaluationId, marks);

        // Try updating first; if no record was modified, perform insert
        boolean updated = markDAO.updateMark(mark);
        if (!updated) {
            return markDAO.insertMark(mark);
        }
        return true;
    }

    /**
     * Saves or updates all evaluation components for a student in a course (Requirement 4: Quiz, Assessment, Mid-sem, Practical, Theory, Final).
     */
    public boolean saveCourseComponentMarks(int studentId, int courseId, Double quiz, Double assignment,
                                           Double midSem, Double practical, Double finalExam)
            throws InvalidMarkException, DatabaseException, SQLException {

        // Validate each component strictly 0-100 (Requirement 4)
        Mark.validateRange(quiz, "Quiz");
        Mark.validateRange(assignment, "Assessment");
        Mark.validateRange(midSem, "Mid-Semester");
        Mark.validateRange(practical, "Practical");
        Mark.validateRange(finalExam, "Final Exam");

        return markDAO.saveCourseComponentMarks(studentId, courseId, quiz, assignment, midSem, practical, finalExam);
    }

    public List<Mark> getMarksForStudent(String studentId) throws DatabaseException, SQLException, InvalidMarkException {
        return markDAO.getMarksByStudent(studentId);
    }

    public List<Mark> getMarksForEvaluation(int evaluationId) throws DatabaseException, SQLException, InvalidMarkException {
        return markDAO.getMarksByEvaluation(evaluationId);
    }

    public List<Mark> getMarksForCourse(int courseId) throws DatabaseException, SQLException {
        return markDAO.getMarksByCourse(courseId);
    }

    public List<Evaluation> getEvaluationsForCourse(int courseId) throws DatabaseException, SQLException {
        return markDAO.getEvaluationsByCourse(courseId);
    }

    public boolean removeMark(String studentId, int evaluationId) throws DatabaseException, SQLException {
        return markDAO.deleteMark(studentId, evaluationId);
    }

    /**
     * Computes CA percentage and eligibility using polymorphic calculator.
     */
    public double calculateCAPercentage(Double quiz, Double assessment, Double midSem, Double practical) {
        Map<String, Double> map = new HashMap<>();
        if (quiz != null) map.put("QUIZ", quiz);
        if (assessment != null) map.put("ASSESSMENT", assessment);
        if (midSem != null) map.put("MID_TERM", midSem);
        if (practical != null) map.put("PRACTICAL", practical);
        return caCalculator.calculateCAPercentage(map);
    }

    public boolean isEligible(double caPercentage) {
        return caCalculator.isEligibleForFinalExam(caPercentage);
    }

    public double calculateFinalMark(double caPercentage, double finalExamMark) {
        return caCalculator.calculateFinalTotal(caPercentage, finalExamMark);
    }
}