package com.faculty.management.model;

import java.time.LocalTime;

/**
 * Represents a single row of the academic Timetable
 * (Day | Time | Course | Lecturer | Theory/Practical | Venue).
 */
public class TimetableEntry {

    public static final String THEORY = "THEORY";
    public static final String PRACTICAL = "PRACTICAL";

    private int timetableId;
    private Course course;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String sessionType;
    private String lecturerName;
    private String venue;
    private int semesterNumber;
    private String academicYear;

    public TimetableEntry() {
    }

    public TimetableEntry(int timetableId, Course course, String dayOfWeek, LocalTime startTime,
                           LocalTime endTime, String sessionType, String lecturerName, String venue,
                           int semesterNumber, String academicYear) {
        this.timetableId = timetableId;
        this.course = course;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionType = sessionType;
        this.lecturerName = lecturerName;
        this.venue = venue;
        this.semesterNumber = semesterNumber;
        this.academicYear = academicYear;
    }

    public boolean isPractical() {
        return PRACTICAL.equalsIgnoreCase(sessionType);
    }

    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
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
}
