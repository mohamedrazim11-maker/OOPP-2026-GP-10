package com.faculty.management.model;

import java.time.LocalDate;

public class Medical {
    private int medicalId;
    private int studentId;
    private String courseId;
    private Integer sessionId;
    private LocalDate medicalDate;
    private String reason;
    private String status; // "PENDING", "APPROVED", "REJECTED"

    public Medical() {
        this.status = "PENDING";
    }

    public Medical(int medicalId, int studentId, String courseId, Integer sessionId, LocalDate medicalDate, String reason, String status) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.sessionId = sessionId;
        this.medicalDate = medicalDate;
        this.reason = reason;
        this.status = status;
    }

    public int getMedicalId() { return medicalId; }
    public void setMedicalId(int medicalId) { this.medicalId = medicalId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public LocalDate getMedicalDate() { return medicalDate; }
    public void setMedicalDate(LocalDate medicalDate) { this.medicalDate = medicalDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
