package com.faculty.management.model;

/**
 * Model class representing Student CA Eligibility.
 */
public class Eligibility {
    private String studentId;
    private String courseId;
    private double caPercentage;
    private String status;

    public Eligibility() {
    }

    public Eligibility(String studentId, String courseId, double caPercentage, String status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.caPercentage = caPercentage;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public double getCaPercentage() {
        return caPercentage;
    }

    public void setCaPercentage(double caPercentage) {
        this.caPercentage = caPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}