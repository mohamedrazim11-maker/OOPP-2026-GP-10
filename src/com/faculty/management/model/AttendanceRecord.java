package com.faculty.management.model;

/** Read-only attendance totals shown to an enrolled student. */
public class AttendanceRecord {
    private final Course course;
    private final int theoryAttended, theoryTotal, practicalAttended, practicalTotal;

    public AttendanceRecord(Course course, int theoryAttended, int theoryTotal, int practicalAttended, int practicalTotal) {
        this.course = course;
        this.theoryAttended = theoryAttended;
        this.theoryTotal = theoryTotal;
        this.practicalAttended = practicalAttended;
        this.practicalTotal = practicalTotal;
    }
    public Course getCourse() { return course; }
    public int getTheoryAttended() { return theoryAttended; }
    public int getTheoryTotal() { return theoryTotal; }
    public int getPracticalAttended() { return practicalAttended; }
    public int getPracticalTotal() { return practicalTotal; }
    public double getTheoryPercentage() { return theoryTotal == 0 ? 0 : theoryAttended * 100.0 / theoryTotal; }
    public double getPracticalPercentage() { return practicalTotal == 0 ? 0 : practicalAttended * 100.0 / practicalTotal; }
    public double getCombinedPercentage() {
        int total = theoryTotal + practicalTotal;
        return total == 0 ? 0 : (theoryAttended + practicalAttended) * 100.0 / total;
    }
}
