package com.faculty.management.model;

/**
<<<<<<< HEAD
 * Represents an academic Course offered by the faculty.
 * Shared reference entity — primarily maintained by Member 1 (Admin),
 * consumed here for Timetable, Grade, and Result calculations.
 * Demonstrates Encapsulation (private fields + getters/setters).
=======
 * Course entity model representing academic modules in the Faculty of Technology.
 * Demonstrates Classes & Objects and Encapsulation.
>>>>>>> test
 */
public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
<<<<<<< HEAD
    private int credits;
    private int theorySessions;
    private int practicalSessions;
    private int semesterNumber;
    private String academicYear;
    private String department;
=======
    private int creditValue;
    private int theoryHours;
    private int practicalHours;
    private String department;
    private String semester;
    private String description;
>>>>>>> test

    public Course() {
    }

<<<<<<< HEAD
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

=======
    public Course(int courseId, String courseCode, String courseName, int creditValue,
                  int theoryHours, int practicalHours, String department, String semester, String description) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditValue = creditValue;
        this.theoryHours = theoryHours;
        this.practicalHours = practicalHours;
        this.department = department;
        this.semester = semester;
        this.description = description;
    }

    public Course(String courseCode, String courseName, int creditValue,
                  int theoryHours, int practicalHours, String department, String semester, String description) {
        this(0, courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
    }

    // Encapsulated Getters and Setters
>>>>>>> test
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

<<<<<<< HEAD
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
=======
    public int getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(int creditValue) {
        this.creditValue = creditValue;
    }

    public int getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(int theoryHours) {
        this.theoryHours = theoryHours;
    }

    public int getPracticalHours() {
        return practicalHours;
    }

    public void setPracticalHours(int practicalHours) {
        this.practicalHours = practicalHours;
>>>>>>> test
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

<<<<<<< HEAD
    @Override
    public String toString() {
        return courseCode + " - " + courseName;
=======
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (" + creditValue + " Credits)";
>>>>>>> test
    }
}
