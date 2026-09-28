package model;

import exception.InvalidMarkException;

public class Mark {
    private int markId;
    private String studentId;
    private int evaluationId;
    private double marksObtained; // Out of 100

    public Mark(int markId, String studentId, int evaluationId, double marksObtained) throws InvalidMarkException {
        this.markId = markId;
        this.studentId = studentId;
        this.evaluationId = evaluationId;
        setMarksObtained(marksObtained);
    }

    public Mark(String studentId, int evaluationId, double marksObtained) throws InvalidMarkException {
        this(0, studentId, evaluationId, marksObtained);
    }

    public int getMarkId() { return markId; }
    public String getStudentId() { return studentId; }
    public int getEvaluationId() { return evaluationId; }
    public double getMarksObtained() { return marksObtained; }

    // Encapsulation with domain rule validation
    public void setMarksObtained(double marksObtained) throws InvalidMarkException {
        if (marksObtained < 0 || marksObtained > 100) {
            throw new InvalidMarkException("Invalid Mark: " + marksObtained + ". Marks must be between 0 and 100.");
        }
        this.marksObtained = marksObtained;
    }
}
