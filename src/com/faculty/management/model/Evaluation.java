package com.faculty.management.model;

public class Evaluation {
    private int evaluationId;
    private String courseId;
    private String type; // QUIZ, ASSESSMENT, MID_TERM, PRACTICAL, FINAL
    private double weightage;

    public Evaluation(int evaluationId, String courseId, String type, double weightage) {
        this.evaluationId = evaluationId;
        this.courseId = courseId;
        this.type = type;
        this.weightage = weightage;
    }

    public int getEvaluationId() { return evaluationId; }
    public String getCourseId() { return courseId; }
    public String getType() { return type; }
    public double getWeightage() { return weightage; }
}
