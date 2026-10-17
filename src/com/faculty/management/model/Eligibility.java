package com.faculty.management.model;

import java.sql.Timestamp;

public class Eligibility {
    private int eligibilityId;
    private int studentId;
    private int courseId;
    private double caPercentage;
    private boolean isEligible;
    private Timestamp updatedAt;

    public Eligibility() {
    }

    public Eligibility(int studentId, int courseId, double caPercentage, boolean isEligible) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.caPercentage = caPercentage;
        this.isEligible = isEligible;
    }

    public Eligibility(int eligibilityId, int studentId, int courseId, double caPercentage, boolean isEligible, Timestamp updatedAt) {
        this.eligibilityId = eligibilityId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.caPercentage = caPercentage;
        this.isEligible = isEligible;
        this.updatedAt = updatedAt;
    }

    public int getEligibilityId() {
        return eligibilityId;
    }

    public void setEligibilityId(int eligibilityId) {
        this.eligibilityId = eligibilityId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public double getCaPercentage() {
        return caPercentage;
    }

    public void setCaPercentage(double caPercentage) {
        this.caPercentage = caPercentage;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }

    public String getStatusString() {
        return isEligible ? "Eligible" : "Not Eligible";
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}