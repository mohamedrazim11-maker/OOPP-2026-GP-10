package com.faculty.management.model;

import java.time.LocalDate;

public class AttendanceSession {
    private int sessionId;
    private String courseId;
    private String sessionType; // "THEORY" or "PRACTICAL"
    private LocalDate sessionDate;
    private double hours;
    private int createdBy;

    public AttendanceSession() {
        this.hours = 2.0;
    }

    public AttendanceSession(int sessionId, String courseId, String sessionType, LocalDate sessionDate, double hours, int createdBy) {
        this.sessionId = sessionId;
        this.courseId = courseId;
        this.sessionType = sessionType;
        this.sessionDate = sessionDate;
        this.hours = hours;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}
