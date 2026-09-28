package com.faculty.management.model;

/**
 * Course entity model representing academic modules in the Faculty of Technology.
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
    }

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

    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (" + creditValue + " Credits)";
    }
}
