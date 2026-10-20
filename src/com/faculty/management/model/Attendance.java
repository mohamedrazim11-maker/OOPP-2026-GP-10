package com.faculty.management.model;

public class Attendance {
    private int recordId;
    private int sessionId;
    private int studentId;
    private String status; // "PRESENT" or "ABSENT"

    public Attendance() {
        this.status = "ABSENT";
    }

    public Attendance(int recordId, int sessionId, int studentId, String status) {
        this.recordId = recordId;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.status = status;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
