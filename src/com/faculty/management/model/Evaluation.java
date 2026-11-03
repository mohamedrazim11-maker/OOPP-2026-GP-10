package com.faculty.management.model;

/**
 * Represents an academic evaluation component (Quiz, Assessment, Mid-term, Practical, Theory, Final).
 * Demonstrates Classes & Objects and Encapsulation.
 */
public class Evaluation {
    private int evaluationId;
    private String courseId;
    private String title;
    private String type; // QUIZ, ASSESSMENT, MID_TERM, PRACTICAL, THEORY, FINAL
    private double weightage;
    private double maxMarks = 100.0;

    public Evaluation() {
    }

    public Evaluation(int evaluationId, String courseId, String type, double weightage) {
        this(evaluationId, courseId, type + " (" + evaluationId + ")", type, weightage, 100.0);
    }

    public Evaluation(int evaluationId, String courseId, String title, String type, double weightage, double maxMarks) {
        this.evaluationId = evaluationId;
        this.courseId = courseId;
        this.title = title;
        this.type = type;
        this.weightage = weightage;
        this.maxMarks = maxMarks;
    }

    public int getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title != null ? title : type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeightage() {
        return weightage;
    }

    public void setWeightage(double weightage) {
        this.weightage = weightage;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    @Override
    public String toString() {
        return getTitle() + " [" + type + " - Weight: " + weightage + "%]";
    }
}
