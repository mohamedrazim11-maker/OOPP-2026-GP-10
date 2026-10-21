package com.faculty.management.model;

import com.faculty.management.exception.InvalidMarkException;

/**
 * Represents student evaluation marks and component scores.
 * Unifies Member 2 evaluation-based mark entries and Continuous Assessment
 * component marks used across grading and calculation modules.
 */
public class Mark {

    private int markId;
    private String studentId;
    private int evaluationId;
    private double marksObtained; // Out of 100

    // Component marks for course CA & Final calculations
    private int courseId;
    private Double quizMarks;
    private Double assignmentMarks;
    private Double midSemMarks;
    private Double practicalMarks;
    private Double finalExamMarks;

    public Mark() {
    }

    public Mark(int markId, String studentId, int evaluationId, double marksObtained) throws InvalidMarkException {
        this.markId = markId;
        this.studentId = studentId;
        this.evaluationId = evaluationId;
        setMarksObtained(marksObtained);
    }

    public Mark(String studentId, int evaluationId, double marksObtained) throws InvalidMarkException {
        this(0, studentId, evaluationId, marksObtained);
    }

    public Mark(int markId, int studentId, int courseId, Double quizMarks, Double assignmentMarks,
                Double midSemMarks, Double practicalMarks, Double finalExamMarks) {
        this.markId = markId;
        this.studentId = String.valueOf(studentId);
        this.courseId = courseId;
        this.quizMarks = quizMarks;
        this.assignmentMarks = assignmentMarks;
        this.midSemMarks = midSemMarks;
        this.practicalMarks = practicalMarks;
        this.finalExamMarks = finalExamMarks;
    }

    /**
     * Computes the Continuous Assessment percentage as the average of
     * whichever CA components (quiz / assignment / mid-semester / practical)
     * were recorded for this student-course pair.
     */
    public double computeCaMarks() {
        double sum = 0;
        int count = 0;
        if (quizMarks != null) {
            sum += quizMarks;
            count++;
        }
        if (assignmentMarks != null) {
            sum += assignmentMarks;
            count++;
        }
        if (midSemMarks != null) {
            sum += midSemMarks;
            count++;
        }
        if (practicalMarks != null) {
            sum += practicalMarks;
            count++;
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public double getFinalExamOrZero() {
        return finalExamMarks == null ? 0.0 : finalExamMarks;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = String.valueOf(studentId);
    }

    public int getStudentIdAsInt() {
        try {
            return studentId == null ? 0 : Integer.parseInt(studentId);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public double getMarksObtained() {
        return marksObtained;
    }

    // Encapsulation with domain rule validation
    public void setMarksObtained(double marksObtained) throws InvalidMarkException {
        if (marksObtained < 0 || marksObtained > 100) {
            throw new InvalidMarkException("Invalid Mark: " + marksObtained + ". Marks must be between 0 and 100.");
        }
        this.marksObtained = marksObtained;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Double getQuizMarks() {
        return quizMarks;
    }

    public void setQuizMarks(Double quizMarks) {
        this.quizMarks = quizMarks;
    }

    public Double getAssignmentMarks() {
        return assignmentMarks;
    }

    public void setAssignmentMarks(Double assignmentMarks) {
        this.assignmentMarks = assignmentMarks;
    }

    public Double getMidSemMarks() {
        return midSemMarks;
    }

    public void setMidSemMarks(Double midSemMarks) {
        this.midSemMarks = midSemMarks;
    }

    public Double getPracticalMarks() {
        return practicalMarks;
    }

    public void setPracticalMarks(Double practicalMarks) {
        this.practicalMarks = practicalMarks;
    }

    public Double getFinalExamMarks() {
        return finalExamMarks;
    }

    public void setFinalExamMarks(Double finalExamMarks) {
        this.finalExamMarks = finalExamMarks;
    }
}
