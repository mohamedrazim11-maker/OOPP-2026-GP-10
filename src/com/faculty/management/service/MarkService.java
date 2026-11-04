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
 * Service orchestrating marks business logic for Member 2.
 * Strictly enforces that all entered marks are between 0 and 100 via InvalidMarkException.
 * Demonstrates Abstraction and Polymorphism via EvaluationCalculator hierarchy.
 */
public class MarkService {

    private final MarkDAO markDAO = new MarkDAO();
    private final EvaluationCalculator caCalculator = new CAEligibilityCalculator();
    private final EvaluationCalculator finalCalculator = new FinalMarkCalculator();

    public boolean saveOrUpdateMark(String studentId, int evaluationId, double marks)
            throws InvalidMarkException, DatabaseException, SQLException {

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }

        // Creating Mark instance triggers domain range validation (Requirement 4: Out of 100)
        Mark mark = new Mark(studentId, evaluationId, marks);

        // Try updating first; if no record was modified, perform insert
        boolean updated = markDAO.updateMark(mark);
        if (!updated) {
            return markDAO.insertMark(mark);
        }
        return true;
    }

    public boolean updateMarkById(int markId, double marks)
            throws InvalidMarkException, DatabaseException, SQLException {
        if (marks < 0.0 || marks > 100.0) {
            throw new InvalidMarkException("Marks must be between 0 and 100.");
        }
        return markDAO.updateMarkById(markId, marks);
    }

    public boolean deleteMarkById(int markId) throws DatabaseException, SQLException {
        return markDAO.deleteMarkById(markId);
    }

    public List<String> getAvailableBatches() {
        return markDAO.getAvailableBatches();
    }

    public int getOrCreateEvaluation(int courseId, String title, String type, double maxMarks) {
        try {
            java.sql.Connection conn = com.faculty.management.config.DatabaseConnection.getInstance().getConnection();
            return markDAO.getOrCreateEvaluation(conn, courseId, title, type, maxMarks);
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Saves or updates all evaluation components for a student in a course (Requirement 4: Quiz, Assessment, Mid-sem, Practical, Final).
     */
    public boolean saveCourseComponentMarks(int studentId, int courseId, Double quiz, Double assignment,
                                           Double midSem, Double practical, Double finalExam)
            throws InvalidMarkException, DatabaseException, SQLException {

        return saveCourseComponentMarks(studentId, courseId, quiz, assignment, midSem, practical, null, finalExam);
    }

    /**
     * Overloaded method supporting all 6 components (Requirement 4: Quiz, Assessment, Mid-sem, Practical, Theory, Final).
     */
    public boolean saveCourseComponentMarks(int studentId, int courseId, Double quiz, Double assignment,
                                           Double midSem, Double practical, Double theory, Double finalExam)
            throws InvalidMarkException, DatabaseException, SQLException {

        // Validate each component strictly 0-100 (Requirement 4)
        Mark.validateRange(quiz, "Quiz");
        Mark.validateRange(assignment, "Assessment");
        Mark.validateRange(midSem, "Mid-Semester");
        Mark.validateRange(practical, "Practical");
        Mark.validateRange(theory, "Theory");
        Mark.validateRange(finalExam, "Final Exam");

        return markDAO.saveCourseComponentMarks(studentId, courseId, quiz, assignment, midSem, practical, theory, finalExam);
    }

    /**
     * Overloaded method supporting String studentId format.
     */
    public boolean saveCourseComponentMarks(String studentId, int courseId, Double quiz, Double assignment,
                                           Double midSem, Double practical, Double theory, Double finalExam)
            throws InvalidMarkException, DatabaseException, SQLException {
        int stdId;
        try {
            stdId = Integer.parseInt(studentId.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            stdId = 32;
        }
        return saveCourseComponentMarks(stdId, courseId, quiz, assignment, midSem, practical, theory, finalExam);
    }

    /**
     * Fetches all marks recorded across all students and evaluations (Requirement 6).
     */
    public List<Mark> getAllMarks() throws DatabaseException, SQLException {
        return markDAO.getAllMarks();
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
        return calculateCAPercentage(quiz, assessment, midSem, practical, null);
    }

    public double calculateCAPercentage(Double quiz, Double assessment, Double midSem, Double practical, Double theory) {
        Map<String, Double> map = new HashMap<>();
        if (quiz != null) map.put("QUIZ", quiz);
        if (assessment != null) map.put("ASSESSMENT", assessment);
        if (midSem != null) map.put("MID_TERM", midSem);
        if (practical != null) map.put("PRACTICAL", practical);
        if (theory != null) map.put("THEORY", theory);
        return caCalculator.calculateCAPercentage(map);
    }

    public boolean isEligible(double caPercentage) {
        return caCalculator.isEligibleForFinalExam(caPercentage);
    }

    public double calculateFinalMark(double caPercentage, double finalExamMark) {
        return caCalculator.calculateFinalTotal(caPercentage, finalExamMark);
    }

    public EvaluationCalculator getCaCalculator() {
        return caCalculator;
    }

    public EvaluationCalculator getFinalCalculator() {
        return finalCalculator;
    }
}
