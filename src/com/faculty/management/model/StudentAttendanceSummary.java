package com.faculty.management.model;

/**
 * Model representing the aggregated Attendance Summary of an Undergraduate.
 * Demonstrates Encapsulation and Data Transfer in OOP.
 */
public class StudentAttendanceSummary {

    private int studentId;
    private String regNo;
    private String studentName;
    private String courseCode;
    private String sessionType; // "THEORY", "PRACTICAL", or "COMBINED"
    private int totalSessions;
    private int attendedSessions;
    private int approvedMedicals;
    private double percentage;
    private boolean eligible;

    public StudentAttendanceSummary() {
    }

    public StudentAttendanceSummary(int studentId, String regNo, String studentName, String courseCode,
                                  String sessionType, int totalSessions, int attendedSessions, int approvedMedicals) {
        this.studentId = studentId;
        this.regNo = regNo;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.sessionType = sessionType;
        this.totalSessions = totalSessions;
        this.attendedSessions = attendedSessions;
        this.approvedMedicals = approvedMedicals;
        calculatePercentageAndEligibility();
    }

    public void calculatePercentageAndEligibility() {
        this.percentage = com.faculty.management.util.AttendanceCalculator.calculateEffectivePercentage(
                attendedSessions, approvedMedicals, totalSessions);
        this.eligible = com.faculty.management.util.AttendanceCalculator.isEligible(this.percentage);
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { 
        this.totalSessions = totalSessions; 
        calculatePercentageAndEligibility();
    }

    public int getAttendedSessions() { return attendedSessions; }
    public void setAttendedSessions(int attendedSessions) { 
        this.attendedSessions = attendedSessions; 
        calculatePercentageAndEligibility();
    }

    public int getApprovedMedicals() { return approvedMedicals; }
    public void setApprovedMedicals(int approvedMedicals) { 
        this.approvedMedicals = approvedMedicals; 
        calculatePercentageAndEligibility();
    }

    public double getPercentage() { return percentage; }
    public boolean isEligible() { return eligible; }
    
    public String getEligibilityStatus() {
        return eligible ? "ELIGIBLE (>= 80%)" : "NOT ELIGIBLE (< 80%)";
    }
}
