package com.faculty.management.model;

/**
 * Represents a Student's enrollment in a Course for a given semester.
 * Drives the grouping used for SGPA (per semester) and CGPA (across
 * semesters) calculations.
 */
public class Enrollment {

    public static final String ONGOING = "ONGOING";
    public static final String COMPLETED = "COMPLETED";
    public static final String REPEAT = "REPEAT";

    private int enrollmentId;
    private int studentId;
    private Course course;
    private int semesterNumber;
    private String academicYear;
    private String status;

    public Enrollment() {
    }

    public Enrollment(int enrollmentId, int studentId, Course course, int semesterNumber,
                       String academicYear, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.course = course;
        this.semesterNumber = semesterNumber;
        this.academicYear = academicYear;
        this.status = status;
    }

    public boolean isCompleted() {
        return COMPLETED.equalsIgnoreCase(status);
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return course == null ? "All modules" : course.getCourseCode() + " — " + course.getCourseName();
    }
}
