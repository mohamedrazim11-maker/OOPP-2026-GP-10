package com.faculty.management.model;

/**
 * Represents the computed Grade for a Student in a Course
 * (Marks -&gt; Grade -&gt; Grade Point), per UGC Commission Circular No. 12-2024.
 * Demonstrates Encapsulation: internal computation is hidden behind
 * GradeCalculator, this class only carries the resulting state.
 */
public class Grade {

    private int gradeId;
    private int studentId;
    private Course course;
    private int semesterNumber;
    private String academicYear;
    private double caMarks;
    private double finalExamMarks;
    private double finalTotalMarks;
    private boolean caEligible;
    private String gradeLetter;
    private double gradePoint;

    public Grade() {
    }

    public Grade(int studentId, Course course, int semesterNumber, String academicYear,
                 double caMarks, double finalExamMarks, double finalTotalMarks, boolean caEligible,
                 String gradeLetter, double gradePoint) {
        this.studentId = studentId;
        this.course = course;
        this.semesterNumber = semesterNumber;
        this.academicYear = academicYear;
        this.caMarks = caMarks;
        this.finalExamMarks = finalExamMarks;
        this.finalTotalMarks = finalTotalMarks;
        this.caEligible = caEligible;
        this.gradeLetter = gradeLetter;
        this.gradePoint = gradePoint;
    }

    /**
     * A grade only contributes credit toward SGPA/CGPA when the student
     * was CA-eligible (CA &gt;= 40%) and actually sat the course, i.e. it is
     * not the placeholder "Not Eligible" (NE) grade.
     */
    public boolean countsTowardGpa() {
        return caEligible && !"NE".equalsIgnoreCase(gradeLetter);
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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

    public double getCaMarks() {
        return caMarks;
    }

    public void setCaMarks(double caMarks) {
        this.caMarks = caMarks;
    }

    public double getFinalExamMarks() {
        return finalExamMarks;
    }

    public void setFinalExamMarks(double finalExamMarks) {
        this.finalExamMarks = finalExamMarks;
    }

    public double getFinalTotalMarks() {
        return finalTotalMarks;
    }

    public void setFinalTotalMarks(double finalTotalMarks) {
        this.finalTotalMarks = finalTotalMarks;
    }

    public boolean isCaEligible() {
        return caEligible;
    }

    public void setCaEligible(boolean caEligible) {
        this.caEligible = caEligible;
    }

    public String getGradeLetter() {
        return gradeLetter;
    }

    public void setGradeLetter(String gradeLetter) {
        this.gradeLetter = gradeLetter;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }
}
