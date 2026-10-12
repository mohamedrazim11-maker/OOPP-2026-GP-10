package com.faculty.management.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a published Result summary for one Student in one semester:
 * the set of course Grades plus the resulting SGPA.
 * CGPA is not stored per-semester; it is derived by GPAService by
 * aggregating every semester's Result for a student (see GPAService).
 */
public class Result {

    private int resultId;
    private int studentId;
    private int semesterNumber;
    private String academicYear;
    private double sgpa;
    private int creditsAttempted;
    private int creditsCompleted;
    private List<Grade> courseGrades = new ArrayList<>();
    /** Populated only by batch queries (joined from `users`) for display; not persisted. */
    private String studentName;

    public Result() {
    }

    public Result(int studentId, int semesterNumber, String academicYear, double sgpa,
                  int creditsAttempted, int creditsCompleted) {
        this.studentId = studentId;
        this.semesterNumber = semesterNumber;
        this.academicYear = academicYear;
        this.sgpa = sgpa;
        this.creditsAttempted = creditsAttempted;
        this.creditsCompleted = creditsCompleted;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public double getSgpa() {
        return sgpa;
    }

    public void setSgpa(double sgpa) {
        this.sgpa = sgpa;
    }

    public int getCreditsAttempted() {
        return creditsAttempted;
    }

    public void setCreditsAttempted(int creditsAttempted) {
        this.creditsAttempted = creditsAttempted;
    }

    public int getCreditsCompleted() {
        return creditsCompleted;
    }

    public void setCreditsCompleted(int creditsCompleted) {
        this.creditsCompleted = creditsCompleted;
    }

    public List<Grade> getCourseGrades() {
        return courseGrades;
    }

    public void setCourseGrades(List<Grade> courseGrades) {
        this.courseGrades = courseGrades;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
