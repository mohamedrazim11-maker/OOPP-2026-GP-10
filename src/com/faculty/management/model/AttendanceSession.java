package com.faculty.management.model;

import java.time.LocalDate;

public class AttendanceSession {
    private int sessionId;
    private String courseId;
    private String sessionType; // "THEORY" or "PRACTICAL"
    private LocalDate sessionDate;
    private int sessionNumber;
    private double hours;
    private int createdBy;

    public AttendanceSession() {
        this.sessionNumber = 1;
        this.hours = 2.0;
    }

    public AttendanceSession(int sessionId, String courseId, String sessionType, int sessionNumber, LocalDate sessionDate, double hours, int createdBy) {
        this.sessionId = sessionId;
        this.courseId = courseId;
        this.sessionType = sessionType;
        this.sessionNumber = sessionNumber;
        this.sessionDate = sessionDate;
        this.hours = hours;
        this.createdBy = createdBy;
    }

    public AttendanceSession(int sessionId, String courseId, String sessionType, LocalDate sessionDate, double hours, int createdBy) {
        this(sessionId, courseId, sessionType, 1, sessionDate, hours, createdBy);
    }

    // Getters and Setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public int getSessionNumber() { return sessionNumber; }
    public void setSessionNumber(int sessionNumber) { this.sessionNumber = sessionNumber; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public double getHours() { return hours; }
    public void setHours(double hours) { this.hours = hours; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}
