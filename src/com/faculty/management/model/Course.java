package com.faculty.management.model;

/**
 * Course entity model representing academic modules in the Faculty of Technology.
<<<<<<< HEAD
 * Shared reference entity maintained by Member 1 (Admin) and consumed across the system.
=======
>>>>>>> Aysha
 * Demonstrates Classes & Objects and Encapsulation.
 */
public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
    private int creditValue;
    private int theoryHours;
    private int practicalHours;
    private String department;
    private String semester;
    private String description;
<<<<<<< HEAD
    private int semesterNumber;
    private String academicYear;
=======
>>>>>>> Aysha

    public Course() {
    }

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

<<<<<<< HEAD
    public Course(int courseId, String courseCode, String courseName, int credits,
                  int theorySessions, int practicalSessions, int semesterNumber,
                  String academicYear, String department) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditValue = credits;
        this.theoryHours = theorySessions;
        this.practicalHours = practicalSessions;
        this.semesterNumber = semesterNumber;
        this.academicYear = academicYear;
        this.department = department;
    }

    public boolean hasPractical() {
        return practicalHours > 0;
    }

=======
>>>>>>> Aysha
    // Encapsulated Getters and Setters
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

    public int getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(int creditValue) {
        this.creditValue = creditValue;
    }

<<<<<<< HEAD
    public int getCredits() {
        return creditValue;
    }

    public void setCredits(int credits) {
        this.creditValue = credits;
    }

=======
>>>>>>> Aysha
    public int getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(int theoryHours) {
        this.theoryHours = theoryHours;
    }

<<<<<<< HEAD
    public int getTheorySessions() {
        return theoryHours;
    }

    public void setTheorySessions(int theorySessions) {
        this.theoryHours = theorySessions;
    }

=======
>>>>>>> Aysha
    public int getPracticalHours() {
        return practicalHours;
    }

    public void setPracticalHours(int practicalHours) {
        this.practicalHours = practicalHours;
    }

<<<<<<< HEAD
    public int getPracticalSessions() {
        return practicalHours;
    }

    public void setPracticalSessions(int practicalSessions) {
        this.practicalHours = practicalSessions;
    }

=======
>>>>>>> Aysha
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

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

<<<<<<< HEAD
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

=======
>>>>>>> Aysha
    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (" + creditValue + " Credits)";
    }
}
