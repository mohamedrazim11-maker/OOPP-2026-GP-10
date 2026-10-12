package com.faculty.management.model;

/**
 * Represents an academic Course offered by the faculty.
 * Shared reference entity — primarily maintained by Member 1 (Admin),
 * consumed here for Timetable, Grade, and Result calculations.
 * Demonstrates Encapsulation (private fields + getters/setters).
 */
public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private int theorySessions;
    private int practicalSessions;
    private int semesterNumber;
    private String academicYear;
    private String department;

    public Course() {
    }

    public Course(int courseId, String courseCode, String courseName, int credits,
                  int theorySessions, int practicalSessions, int semesterNumber,
                  String academicYear, String department) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.theorySessions = theorySessions;
        this.practicalSessions = practicalSessions;
        this.semesterNumber = semesterNumber;
        this.academicYear = academicYear;
        this.department = department;
    }

    public boolean hasPractical() {
        return practicalSessions > 0;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getTheorySessions() {
        return theorySessions;
    }

    public void setTheorySessions(int theorySessions) {
        this.theorySessions = theorySessions;
    }

    public int getPracticalSessions() {
        return practicalSessions;
    }

    public void setPracticalSessions(int practicalSessions) {
        this.practicalSessions = practicalSessions;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}
