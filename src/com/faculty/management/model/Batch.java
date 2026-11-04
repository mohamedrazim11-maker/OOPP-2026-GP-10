package com.faculty.management.model;

import java.time.LocalDateTime;

/**
 * Represents an academic intake or cohort batch for Undergraduates.
 * Demonstrates Core OOP Principles:
 * - Encapsulation: Private member variables exposed via getters and setters.
 * - Classes & Objects: Clear domain model representation.
 */
public class Batch {

    private int batchId;
    private String batchName;       // e.g. "2025/2026", "2024/2025", "2021/2022"
    private String academicYear;    // e.g. "2025/2026"
    private String description;     // e.g. "Faculty of Technology 7th Intake"
    private String status;          // "ACTIVE", "UPCOMING", "COMPLETED"
    private int studentCount;       // Number of undergraduates assigned to this batch
    private String createdAt;

    public Batch() {
        this.status = "ACTIVE";
    }

    public Batch(int batchId, String batchName, String academicYear, String description, String status) {
        this.batchId = batchId;
        this.batchName = batchName;
        this.academicYear = academicYear;
        this.description = description;
        this.status = status != null ? status : "ACTIVE";
    }

    public Batch(int batchId, String batchName, String academicYear, String description, String status, int studentCount, String createdAt) {
        this.batchId = batchId;
        this.batchName = batchName;
        this.academicYear = academicYear;
        this.description = description;
        this.status = status != null ? status : "ACTIVE";
        this.studentCount = studentCount;
        this.createdAt = createdAt;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return batchName != null ? batchName : "";
    }
}
